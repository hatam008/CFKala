package controler;

import ModelPackage.Product.Product;
import ModelPackage.System.exeption.account.UserNotAvailableException;
import ModelPackage.Users.Cart;
import ModelPackage.Users.Customer;
import ModelPackage.Users.SubCart;
import ModelPackage.Users.User;
import View.PrintModels.UserFullPM;

import java.util.ArrayList;
import java.util.List;

public class AccountController extends Controller {

    public void usernameInitialCheck(String username){
        if (accountManager.isUsernameAvailable(username)){
            throw new UserNotAvailableException();
        }
    }

    public void createAccount(String[] info, String type) throws UserNotAvailableException {
        accountManager.createAccount(info, type);
        if (type.equalsIgnoreCase("customer")){
            addCartToCustomer(info[0], cart);
        }
    }

    public String login(String username, String password) throws NotVerifiedSeller, UserNotAvailableException {
        return accountManager.login(username, password);
    }

    public UserFullPM viewPersonalInfo(String username){
        User user = accountManager.viewPersonalInfo(username);
        return new UserFullPM(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getClass().getName()
        );
    }

    public void editPersonalInfo(String username, UserEditAttributes editAttributes){
        accountManager.changeInfo(username, editAttributes);
    }

    public void logout(String username) throws UserNotAvailableException {
        accountManager.logout(username);
    }

    private void addCartToCustomer(String username, Cart cart) throws UserNotAvailableException {
        Customer customer = (Customer)accountManager.getUserByUsername(username);
        Cart previousCart = customer.getCart();

        boolean added;
        for (SubCart subCart : cart.getSubCarts()) {
            added = false;
            Product product = subCart.getProduct();
            for (SubCart previousCartSubCart : previousCart.getSubCarts()) {
                Product previousCartProduct = previousCartSubCart.getProduct();
                if (previousCartProduct.getId() == product.getId() &&
                        previousCartSubCart.getSeller().getUsername().equals(subCart.getSeller().getUsername())){
                    previousCartSubCart.setAmount(previousCartSubCart.getAmount() + subCart.getAmount());
                    added = true;
                    break;
                }
            }
            if (!added){
                previousCart.getSubCarts().add(subCart);
            }
        }
    }
}
