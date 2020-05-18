package controler;

import ModelPackage.Product.Comment;
import ModelPackage.Product.CommentStatus;
import ModelPackage.Product.Product;
import ModelPackage.System.FilterManager;
import ModelPackage.System.ProductManager;
import ModelPackage.System.exeption.account.ProductNotHaveSellerException;
import ModelPackage.System.exeption.category.NoSuchACategoryException;
import ModelPackage.System.exeption.filters.InvalidFilterException;
import ModelPackage.System.exeption.product.NoSuchAProductException;
import ModelPackage.Users.Seller;
import ModelPackage.Users.User;
import View.FilterPackage;
import View.PrintModels.CommentPM;
import View.PrintModels.FullProductPM;
import View.PrintModels.MiniProductPM;
import View.SortPackage;
import controler.exceptions.ProductsNotBelongToUniqueCategoryException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductController extends Controller{
    private static ProductController productController = new ProductController();

    public static ProductController getInstance() {
        return productController;
    }

    public List<MiniProductPM> showAllProducts(SortPackage sortPackage, FilterPackage filterPackage) throws NoSuchACategoryException, InvalidFilterException {
        List<Product> list = ProductManager.getInstance().getAllProductsActive();
        int[] priceRange = {filterPackage.getDownPriceLimit(),filterPackage.getUpPriceLimit()};
        ArrayList<Product> products = FilterManager.updateFilterList(filterPackage.getCategoryId(), filterPackage.getActiveFilters(), priceRange);
        sortManager.sort(products,sortPackage.getSortType());
        if (!sortPackage.isAscending()) Collections.reverse(products);
        List<MiniProductPM> toReturn = new ArrayList<>();
        for (Product product : products) {
            toReturn.add(createMiniProductPM(product));
        }

        return toReturn;
    }

    public void assignComment(String[] data) throws NoSuchAProductException {
        String userId = data[0];
        String commentTitle = data[1];
        String commentText = data[2];
        int productId = Integer.parseInt(data[3]);
        Comment comment = new Comment(userId, commentTitle, commentText,
                CommentStatus.NOT_VERIFIED,
                csclManager.boughtThisProduct(productId, userId));
        comment.setProduct(productManager.findProductById(productId));
        csclManager.createComment(comment);
    }

    public List<CommentPM> viewProductComments(int productId) throws NoSuchAProductException {
        Comment[] comments = productManager.showComments(productId);
        List<CommentPM> commentPMs = new ArrayList<>();
        for (Comment comment : comments) {
            commentPMs.add(new CommentPM(comment.getUserId(),
                    comment.getTitle(),
                    comment.getText()));
        }
        return commentPMs;
    }

    public FullProductPM viewAttributes(int productId) throws NoSuchAProductException {
        return createFullProductPM(productId);
    }

    public FullProductPM[] compareProducts(String[] data)
            throws NoSuchAProductException, ProductsNotBelongToUniqueCategoryException {
        int firstProductId = Integer.parseInt(data[0]);
        int secondProductId = Integer.parseInt(data[1]);
        checkIfTwoProductsDoesNotBelongToUniqueCategory(firstProductId, secondProductId);
        FullProductPM[] fullProductPMs = new FullProductPM[2];
        fullProductPMs[0] = createFullProductPM(firstProductId);
        fullProductPMs[1] = createFullProductPM(secondProductId);
        return fullProductPMs;
    }

    public MiniProductPM digest(int productId) throws NoSuchAProductException {
        Product product = productManager.findProductById(productId);
        return createMiniProductPM(product);
    }


    /*TODO : Change */
    public void addToCart(String[] data) throws Exception {
        String userName = data[0];
        User user = accountManager.getUserByUsername(userName);
        if (user.isHasSignedIn()) {
            int productId = Integer.parseInt(data[1]);
            String sellerUserName = data[2];
            int amount = Integer.parseInt(data[3]);
            cartManager.addProductToCart(user.getCart(), sellerUserName, productId, amount);
        }
    }

    public Seller selectSeller(String[] data) throws NoSuchAProductException,
            ProductNotHaveSellerException {
        int productId = Integer.parseInt(data[0]);
        String sellerUserName = data[1];
        return productManager.showSellerOfProduct(productId, sellerUserName);
    }

    private void checkIfTwoProductsDoesNotBelongToUniqueCategory(int firstProductId, int secondProductId)
            throws ProductsNotBelongToUniqueCategoryException, NoSuchAProductException {
        Product firstProduct = productManager.findProductById(firstProductId);
        Product secondProduct = productManager.findProductById(secondProductId);
        if (firstProduct.getId() != (secondProduct.getId()))
            throw new ProductsNotBelongToUniqueCategoryException(firstProduct.getId(), secondProduct.getId());
    }

    private FullProductPM createFullProductPM(int productId) throws NoSuchAProductException {
        Product product = productManager.findProductById(productId);
        return new FullProductPM(createMiniProductPM(product),
                productManager.allFeaturesOf(product));
    }

    private MiniProductPM createMiniProductPM(Product product) {
        return new MiniProductPM(product.getName(), product.getId(),
                product.getCategory().getName(),
                product.getStock(), product.getPrices(), product.getCompany(),
                product.getTotalScore(), product.getDescription());
    }

}
