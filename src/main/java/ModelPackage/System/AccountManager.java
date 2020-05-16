package ModelPackage.System;

import ModelPackage.System.database.DBManager;
import ModelPackage.System.exeption.account.*;
import ModelPackage.Users.*;


public class AccountManager {
    private static AccountManager accountManager = null;
    private AccountManager(){

    }
    public static AccountManager getInstance(){
        if (accountManager == null)
            accountManager = new AccountManager();
        return accountManager;
    }

    private RequestManager requestManager = RequestManager.getInstance();
    private CSCLManager csclManager = CSCLManager.getInstance();

    public void createAccount(String[] info, String type) {
        switch (type){
            case "seller" : createSeller(info); break;
            case "manager" : Manager manager = createManager(info);DBManager.save(manager); break;
            case  "customer" : Customer customer = createCustomer(info);DBManager.save(customer); break;
        }
    }

    private void createSeller(String[] info) {
        Seller seller = new Seller(
                info[0],
                info[1],
                info[2],
                info[3],
                info[4],
                info[5],
                new Cart(),
                csclManager.getCompanyById(Integer.parseInt(info[6])),
                Long.parseLong(info[7]));
        String requestStr = String.format("%s has requested to create a seller with email %s", info[0], info[4]);
        requestManager.addRequest(new Request(info[0],RequestType.REGISTER_SELLER,requestStr,seller));
    }

    public Manager createManager(String[] info){
        return new Manager(info[0],
                info[1],
                info[2],
                info[3],
                info[4],
                info[5],
                new Cart()
        );
    }

    private Customer createCustomer(String[] info){
        return new Customer(
                info[0],
                info[1],
                info[2],
                info[3],
                info[4],
                info[5],
                new Cart(),
                Long.parseLong(info[6])
        );
    }

    public String login(String username,String password) throws NotVerifiedSeller, UserNotAvailableException {
        User user = getUserByUsername(username);

        if (isCorrectPassword(user, password)){
            Customer customer = DBManager.load(Customer.class,username);
            if (customer != null) return "Customer";
            Seller seller = DBManager.load(Seller.class,username);
            if (seller != null) if (seller.getVerified())
                return "Seller";
            else
                throw new NotVerifiedSeller();
            Manager manager = DBManager.load(Manager.class,username);
            if (manager != null) {
                return "Manager";
            }
        } else {
            throw new WrongPasswordException(username);
        }
        return "";
    }

    public User viewPersonalInfo(String username) throws UserNotAvailableException {
        User user = getUserByUsername(username);
        checkIfUserHasLoggedIn(user);
        return user;
    }

    public void changeInfo(String[] info) throws UserNotAvailableException {
        String username = info[0];
        String type = info[1];
        String newInfo = info[2];

        User user = getUserByUsername(username);

        switch (type){
            case "password" :
                changePass(newInfo, user);
                break;
            case "firstName" :
                changeFName(newInfo, user);
                break;
            case "lastName" :
                changeLName(newInfo, user);
                break;
            case "email" :
                changeEmail(newInfo, user);
                break;
            case "phoneNumber" :
                changePhone(newInfo, user);
                break;
        }
        DBManager.save(user);
    }

    private void changePhone(String newInfo, User user) {
        if (user.getPhoneNumber().equals(newInfo)){
            throw new SameInfoException("Phone Number");
        }
        user.setPhoneNumber(newInfo);
    }

    private void changeEmail(String newInfo, User user) {
        if (user.getEmail().equals(newInfo)){
            throw new SameInfoException("Email");
        }
        user.setEmail(newInfo);
    }

    private void changeLName(String newInfo, User user) {
        if (user.getLastName().equals(newInfo)){
            throw new SameInfoException("Last Name");
        }
        user.setLastName(newInfo);
    }

    private void changeFName(String newInfo, User user) {
        if (user.getFirstName().equals(newInfo)){
            throw new SameInfoException("First Name");
        }
        user.setFirstName(newInfo);
    }

    private void changePass(String newInfo, User user) {
        if (user.getPassword().equals(newInfo)){
            throw new SameInfoException("Password");
        }
        user.setPassword(newInfo);
    }

    public void logout(String username) throws UserNotAvailableException {
        User user = getUserByUsername(username);
        checkIfUserHasLoggedIn(user);
        user.setHasSignedIn(false);
    }

    private boolean isCorrectPassword(User username,String password) {
        return username.getPassword().equals(password);
    }

    public User getUserByUsername(String username) throws UserNotAvailableException {
        User user = DBManager.load(User.class,username);
        if (user == null)
            throw new UserNotAvailableException();
        return user;
    }

    public boolean isUsernameAvailable(String username){
        User user = DBManager.load(User.class,username);
        return user != null;
    }

    private void checkIfUserHasLoggedIn(User user){
        if (!user.isHasSignedIn()){
            throw new UserNotLoggedInException();
        }
    }

}
