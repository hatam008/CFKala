package ModelPackage.System;

import ModelPackage.Maps.SellerIntegerMap;
import ModelPackage.Product.Product;
import ModelPackage.System.database.DBManager;
import ModelPackage.System.exeption.cart.NoSuchAProductInCart;
import ModelPackage.System.exeption.cart.NotEnoughAmountOfProductException;
import ModelPackage.System.exeption.cart.ProductExistedInCart;
import ModelPackage.System.exeption.product.NoSuchAProductException;
import ModelPackage.Users.Cart;
import ModelPackage.Users.Seller;
import ModelPackage.Users.SubCart;
import lombok.Data;

@Data
public class CartManager {
    private static CartManager cartManager = new CartManager();

    public static CartManager getInstance() {
        return cartManager;
    }

    private CartManager() {}

    public void addProductToCart(Cart cart, String sellerId, int productId, int amount) throws Exception {
        checkIfProductExistsInCart(cart, productId);
        checkIfThereIsEnoughAmountOfProduct(productId, sellerId, amount);
        Product product = ProductManager.getInstance().findProductById(productId);
        Seller seller = (Seller) AccountManager.getInstance().getUserByUsername(sellerId);
        SubCart subCart = new SubCart(product, seller, amount);

        cart.getSubCarts().add(subCart);
        cart.setTotalPrice(calculateTotalPrice(cart));

        DBManager.save(subCart);
        DBManager.save(cart);
    }

    private long calculateTotalPrice(Cart cart) {
        long total = 0;
        CSCLManager csclManager = CSCLManager.getInstance();
        for (SubCart subCart : cart.getSubCarts()) {
            total += csclManager.findPrice(subCart);
        }
        return total;
    }

    public void checkIfThereIsEnoughAmountOfProduct(int productId, String sellerId, int amount)
            throws NotEnoughAmountOfProductException, NoSuchAProductException {
        Product product = ProductManager.getInstance().findProductById(productId);
        for (SellerIntegerMap map : product.getStock()) {
            if (map.thisIsTheMapKey(sellerId)){
                if (map.getInteger()<amount) throw new NotEnoughAmountOfProductException(map.getInteger());
            }
        }
    }

    private void checkIfProductExistsInCart(Cart cart, int productId) throws ProductExistedInCart {
        for (SubCart subCart : cart.getSubCarts()) {
            if (subCart.getProduct().getId() == productId) throw new ProductExistedInCart(productId);
        }
    }

    public SubCart getSubCartByProductId(Cart cart, int productId) throws NoSuchAProductInCart {
        for (SubCart subCart : cart.getSubCarts()) {
            if (subCart.getProduct().getId() == productId) return subCart;
        }
        throw new NoSuchAProductInCart(productId);
    }

    public void deleteProductFromCart(Cart cart, int productId, String sellerId) throws NoSuchAProductInCart {
        SubCart subCart = getSubCartByProductId(cart, productId);
        cart.getSubCarts().remove(subCart);
        DBManager.delete(subCart);
        DBManager.save(cart);
    }

    public void changeProductAmountInCart(Cart cart, int productId, String sellerId, int change)
            throws Exception {
        SubCart subCart = getSubCartByProductId(cart, productId);
        int previousAmount = subCart.getAmount();
        checkIfThereIsEnoughAmountOfProduct(productId, sellerId, previousAmount + change);
        if (previousAmount + change == 0){
            cart.getSubCarts().remove(subCart);
        } else {
            subCart.setAmount(previousAmount + change);
        }
        cart.setTotalPrice(calculateTotalPrice(cart));
        DBManager.save(cart);
    }

}
