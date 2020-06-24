package ModelPackage.System;

import ModelPackage.Off.Off;
import ModelPackage.Product.*;
import ModelPackage.System.database.DBManager;
import ModelPackage.System.database.HibernateUtil;
import ModelPackage.System.editPackage.ProductEditAttribute;
import ModelPackage.System.exeption.product.*;
import ModelPackage.Users.Request;
import ModelPackage.Users.RequestType;
import ModelPackage.Users.Seller;
import View.PrintModels.MicroProduct;
import lombok.Data;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class ProductManager {
    private static ProductManager productManager = null;

    public List<Product> getAllProductsActive() {
        Session session = HibernateUtil.getSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> root = criteriaQuery.from(Product.class);
        criteriaQuery.select(root);
        criteriaQuery.where(
                criteriaBuilder.equal(root.get("productStatus").as(ProductStatus.class), ProductStatus.VERIFIED)
        );
        Query<Product> query = session.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private ProductManager() {
    }

    public static ProductManager getInstance(){
        if(productManager == null){
            return productManager = new ProductManager();
        }
        else{
            return productManager;
        }
    }

    public int createProduct(Product product, String sellerId) {
        product.setView(0);
        product.setBoughtAmount(0);
        product.setTotalScore(0);
        DBManager.save(product);
        String requestStr = String.format("%s has requested to create Product \"%s\" with id %s",sellerId,product.getName(),product.getId());
        Seller seller = DBManager.load(Seller.class,sellerId);
        Request request = new Request(seller.getUsername(), RequestType.CREATE_PRODUCT,requestStr,product);
        RequestManager.getInstance().addRequest(request);
        seller.addRequest(request);
        DBManager.save(request);
        return product.getId();
    }

    public void editProduct(ProductEditAttribute edited, String editor) throws NoSuchAProductException, EditorIsNotSellerException {
        String requestStr = String.format("%s has requested to edit Product \"%s\" with id %s",edited,edited.getName(),edited.getId());
        Product product = findProductById(edited.getSourceId());
        System.out.println(edited.getName());
        DBManager.save(edited);
        checkIfEditorIsASeller(editor,product);
        product.setProductStatus(ProductStatus.UNDER_EDIT);
        Request request = new Request(editor,RequestType.CHANGE_PRODUCT,requestStr,edited);
        RequestManager.getInstance().addRequest(request);
        Seller seller = DBManager.load(Seller.class, editor);
        if (seller != null) {
            seller.addRequest(request);
            DBManager.save(seller);
        }
    }

    private void checkIfEditorIsASeller(String username,Product product) throws EditorIsNotSellerException {
        if (!product.hasSeller(username))throw new EditorIsNotSellerException();
    }

    public void changeAmountOfStock(int productId, String sellerId, int amount) throws NoSuchSellerException {
        Product product = DBManager.load(Product.class,productId);
        SellPackage sellPackage = product.findPackageBySeller(sellerId);
        int stock = sellPackage.getStock();
        stock += amount;
        if (stock < 0) stock = 0;
        sellPackage.setStock(stock);
        DBManager.save(sellPackage);
    }

    public Product findProductById(int id) throws NoSuchAProductException {
        Product product = DBManager.load(Product.class,id);
        if (product == null) {
            throw new NoSuchAProductException(Integer.toString(id));
        }
        return product;
    }

    public ArrayList<MicroProduct> findProductByName(String name){
        ArrayList<MicroProduct> list = new ArrayList<>();
        for (Product product : getAllProductsActive()) {
            if(product.getName().toLowerCase().contains(name.toLowerCase()))
                list.add(new MicroProduct(product.getName(),product.getId()));
        }
        return list;
    }

    // TODO: 6/23/2020 Add to Product Digest
    public void addView(int productId) throws NoSuchAProductException {
        Product product = findProductById(productId);
        product.setView(product.getView()+1);
        DBManager.save(product);
    }

    // TODO: 6/23/2020 Add to Purchase
    public void addBought(int productId) throws NoSuchAProductException {
        Product product = findProductById(productId);
        product.setBoughtAmount(product.getBoughtAmount()+1);
        DBManager.save(product);
    }

    public void assignAComment(int productId, Comment comment) throws NoSuchAProductException {
        Product product = findProductById(productId);
        List<Comment> comments = product.getAllComments();
        comments.add(comment);
        product.setAllComments(comments);
        DBManager.save(product);
        DBManager.save(comment);
    }

    public void assignAScore(int productId, Score score) throws NoSuchAProductException {
        Product product = findProductById(productId);
        ArrayList<Score> scores = (ArrayList<Score>) product.getAllScores();
        int amount = scores.size();
        scores.add(score);
        product.setAllScores(scores);
        product.setTotalScore((product.getTotalScore()*amount + score.getScore())/(amount+1));
        DBManager.save(product);
    }

    public Comment[] showComments(int productId) throws NoSuchAProductException {
        Product product = findProductById(productId);
        List<Comment> comments = new CopyOnWriteArrayList<>(product.getAllComments());
        Iterator<Comment> iterator = comments.iterator();
        while (iterator.hasNext()) {
            Comment comment = iterator.next();
            if (!comment.getStatus().equals(CommentStatus.VERIFIED)) comments.remove(comment);
        }
        Comment[] toReturn = new Comment[comments.size()];
        comments.toArray(toReturn);
        return toReturn;
    }

    public boolean doesThisProductExist(int productId) throws NoSuchAProductException {
        Product product = findProductById(productId);
        return (product != null);
    }

    public void checkIfThisProductExists(int productId) throws NoSuchAProductException{
        Product product = findProductById(productId);
        if (product == null) throw new NoSuchAProductException(Integer.toString(productId));
    }

    public void deleteProduct(int productId) throws NoSuchAProductException {
        Product product = findProductById(productId);
        product.getPackages().forEach(sellPackage -> {
            Seller seller = sellPackage.getSeller();
            seller.getPackages().remove(sellPackage);
            DBManager.save(seller);
            if (sellPackage.isOnOff()) {
                Off off = sellPackage.getOff();
                off.getProducts().remove(product);
                DBManager.save(off);
            }
        });
        product.setPackages(null);
        product.setCompany(null);
        Category category = product.getCategory();
        category.getAllProducts().remove(product);
        DBManager.save(category);
        DBManager.save(product);
        DBManager.delete(product);
    }

    public void changePrice(Product product, int newPrice, String username) throws NoSuchSellerException {
        SellPackage sellPackage = product.findPackageBySeller(username);
        if (newPrice > 0) {
            sellPackage.setPrice(newPrice);
            DBManager.save(sellPackage);
        }
        if (newPrice < product.getLeastPrice()) {
            product.setLeastPrice(newPrice);
        }
        DBManager.save(product);
    }

    public void changeStock(Product product, int newStock, String username) throws NoSuchSellerException {
        SellPackage sellPackage = product.findPackageBySeller(username);
        if (newStock > 0) {
            sellPackage.setStock(newStock);
            DBManager.save(sellPackage);
        }
    }

    public void deleteProductCategoryOrder(Product product){
        DBManager.delete(product);
    }

    public HashMap<String,String> allFeaturesOf(Product product){
        HashMap<String,String> allFeatures = new HashMap<>(product.getPublicFeatures());
        allFeatures.putAll(product.getSpecialFeatures());
        return allFeatures;
    }

    public void addASellerToProduct(Product product,Seller seller,int amount,int price) {
        SellPackage sellPackage = new SellPackage(product,seller,price,amount,null, false,true);
        DBManager.save(sellPackage);
        int currentLeast = product.getLeastPrice();
        if (currentLeast > price) {
            product.setLeastPrice(price);
        }
        product.getPackages().add(sellPackage);
        seller.getPackages().add(sellPackage);
        DBManager.save(seller);
        DBManager.save(product);
    }

    public Seller bestSellerOf(Product product){
        Seller seller = new Seller();
        int pricy = 2000000000;
        for (SellPackage aPackage : product.getPackages()) {
            int price = aPackage.getPrice();
            if (price < pricy){
                seller = aPackage.getSeller();
                pricy = price;
            }
        }
        return seller;
    }

    public List<Product> getAllOffFromActiveProducts(){
        List<Product> toReturn = new ArrayList<>();
        for (Product product : getAllProductsActive()) {
            if (product.isOnOff())toReturn.add(product);
        }
        return toReturn;
    }
}
