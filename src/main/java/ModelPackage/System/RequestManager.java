package ModelPackage.System;

import ModelPackage.Off.Off;
import ModelPackage.Off.OffStatus;
import ModelPackage.Product.Comment;
import ModelPackage.Product.CommentStatus;
import ModelPackage.Product.Product;
import ModelPackage.Product.ProductStatus;
import ModelPackage.System.database.DBManager;
import ModelPackage.System.editPackage.ProductEditAttribute;
import ModelPackage.System.exeption.category.NoSuchACategoryException;
import ModelPackage.System.exeption.category.NoSuchAProductInCategoryException;
import ModelPackage.System.exeption.product.AlreadyASeller;
import ModelPackage.System.exeption.product.NoSuchAProductException;
import ModelPackage.System.exeption.request.NoSuchARequestException;
import ModelPackage.Users.Request;
import ModelPackage.Users.RequestType;
import ModelPackage.Users.Seller;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestManager {
    private List<Request> requests;
    private static RequestManager requestManager = null;

    private RequestManager(){
        requests =  new ArrayList<>();
    }

    public void addRequest(Request request){
        DBManager.save(request);
    }

    public Request findRequestById(int id)
            throws NoSuchARequestException {
        Request request = DBManager.load(Request.class,id);
        if (request == null) throw new NoSuchARequestException(id);
        else return request;
    }

    public void accept(int requestId)
            throws NoSuchARequestException, AlreadyASeller, NoSuchAProductException {
        Request request = findRequestById(requestId);
        RequestType type = request.getRequestType();
        switch (type){
            case CREATE_PRODUCT:
                acceptCreateProduct(request);
            case CHANGE_PRODUCT:
                acceptEditProduct(request);
                break;
            case CREATE_OFF:
                acceptCreateOff(request);
                break;
            case EDIT_OFF:
                acceptEditOff(request);
            case ASSIGN_COMMENT:
                acceptAssignComment(request);
                break;
            case REGISTER_SELLER:
                acceptSeller(request);
                break;
        }
    }

    private void acceptCreateProduct(Request request) throws AlreadyASeller{
        Product product = request.getProduct();
        product.setProductStatus(ProductStatus.VERIFIED);
        Seller seller = request.getSeller();
        seller.getProducts().add(product);
        ProductManager.getInstance().addASellerToProduct(product,seller);
        DBManager.save(seller);
        DBManager.delete(request);
    }

    private void acceptEditProduct(Request request) throws NoSuchAProductException {
        ProductEditAttribute editAttribute = request.getProductEditAttribute();
        Product product = ProductManager.getInstance().findProductById(editAttribute.getSourceId());
        if (editAttribute.getName() != null) {
            product.setName(editAttribute.getName());
            DBManager.save(product);
        }else if (editAttribute.getPublicFeatureTitle() != null) {
            editPublicFeatureProduct(product,editAttribute);
        }else if (editAttribute.getSpecialFeatureTitle() != null) {
            editSpecialFeatureProduct(product,editAttribute);
        }else if (editAttribute.getNewCategoryId() != 0){
            try {
                CategoryManager.getInstance().editProductCategory(product.getId(),product.getCategory().getId(),editAttribute.getNewCategoryId());
            } catch (Exception e) {}
        }
    }

    private void editPublicFeatureProduct(Product product,ProductEditAttribute editAttribute){
        String title = editAttribute.getPublicFeatureTitle();
        String feature = editAttribute.getPublicFeature();
        Map<String, String> features = product.getPublicFeatures();
        if (features.containsKey(title)){
            features.replace(title,feature);
        }
        DBManager.save(product);
    }

    private void editSpecialFeatureProduct(Product product,ProductEditAttribute editAttribute){
        String title = editAttribute.getSpecialFeatureTitle();
        String feature = editAttribute.getSpecialFeature();
        Map<String, String> features = product.getSpecialFeatures();
        if (features.containsKey(title)){
            features.replace(title,feature);
        }
        DBManager.save(product);
    }

    private void acceptCreateOff(Request request){
        Off off = request.getOff();
        off.setOffStatus(OffStatus.ACCEPTED);
        Seller seller = off.getSeller();
        List<Off> offs = seller.getOffs();
        offs.add(off);
        seller.setOffs(offs);
    }

    private void addOffToProducts(Off off){
        for (Product product : off.getProducts()) {
            product.setOff(off);
            product.setOnOff(true);
        }
    }

    private void acceptEditOff(Request request){
        Off off = request.getOff();
        Seller seller = off.getSeller();
        List<Off> offs = seller.getOffs();
        Off offf = off;/* TODO : = CLCSManager.getInstance().findOffById(off.getId());*/
        offs.set(offs.indexOf(offf),off);
    }

    private void acceptAssignComment(Request request) throws NoSuchAProductException {
        Comment comment = request.getComment();
        comment.setStatus(CommentStatus.VERIFIED);
        ProductManager.getInstance().assignAComment(comment.getId(),comment);
    }

    /*TODO*/
    private void acceptSeller(Request request) {
        Seller seller = request.getSeller();
        /*AccountManager.getInstance().getUsers().add(seller);*/

        String sellerJson = new Gson().toJson(seller);
        try {
            FileWriter fileWriter = new FileWriter("src/main/resources/users.user",true);
            fileWriter.write(sellerJson);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decline(int requestId) throws NoSuchARequestException {
        Request request = findRequestById(requestId);
        /* TODO : DELETE Extra Data from DB */
        DBManager.delete(request);
    }

    public static RequestManager getInstance(){
        if(requestManager == null){
            requestManager = new RequestManager();
        }
        return requestManager;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void clear(){
        requests.clear();
    }
}
