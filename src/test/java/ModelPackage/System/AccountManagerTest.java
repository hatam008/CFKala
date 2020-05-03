package ModelPackage.System;

import ModelPackage.System.exeption.account.SameInfoException;
import ModelPackage.System.exeption.account.WrongPasswordException;
import ModelPackage.Users.Cart;
import ModelPackage.Users.Manager;
import ModelPackage.Users.User;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;

public class AccountManagerTest {

    AccountManager accountManager = AccountManager.getInstance();
    ArrayList<User> users = (ArrayList<User>) accountManager.getUsers();

    User marmof;
    Manager hatam;

    {
        marmof = new User("marmofayezi",
                "marmof.ir",
                "Mohamadreza",
                "Mofayezi",
                "marmof@gmail.com",
                "09121232222",
                new Cart()
        );
        hatam = new Manager(
                "hatam008",
                "hatam008@kimi",
                "Ali",
                "Hatami",
                "hatam008@yahoo.com",
                "09121351223",
                new Cart()
        );

        users.add(marmof);
        users.add(hatam);
    }

    @Test
    public void changeInfo() throws SameInfoException {
        String[] info = {"marmofayezi","password", "marmof.ir", "marmof.com"};
        accountManager.getUsers().add(marmof);
        accountManager.changeInfo(info);
        String expected = "marmof.com";
        Assert.assertEquals(expected,marmof.getPassword());
    }

    @Test(expected = SameInfoException.class)
    public void changeInfoSameInfo() throws SameInfoException {
        String[] info = {"marmofayezi","password", "marmof.ir", "marmof.ir"};
        accountManager.getUsers().add(marmof);
        accountManager.changeInfo(info);
    }

    @Test
    public void createAccount_Customer() {
        String[] info = {
                "marmofayezi",
                "marmof.ir",
                "Mohamadreza",
                "Mofayezi",
                "marmof@gmail.com",
                "09121232222",
                "1000000"
        };
        accountManager.createAccount(info,"customer");
        User actual = accountManager.getUserByUsername("marmofayezi");
        User expected = marmof;
    }

    @Test
    public void createManager(){
        String[] info = {
                "hatam008",
                "hatam008@kimi",
                "Ali",
                "Hatami",
                "hatam008@yahoo.com",
                "09121351223"
        };
        Manager actual = accountManager.createManager(info);
        Assert.assertEquals(hatam,actual);
    }

    @Test
    public void login() throws WrongPasswordException {
        accountManager.login("marmofayezi","marmof.ir");
        boolean actual = marmof.isHasSignedIn();
        Assert.assertTrue(actual);
    }

    @Test(expected = WrongPasswordException.class)
    public void loginWithWrongPassword() throws WrongPasswordException {
        accountManager.login("marmofayezi","marmof.com");
    }

    @Test
    public void logout() {
        accountManager.logout("marmofayezi");
        boolean actual = marmof.isHasSignedIn();
        Assert.assertFalse(actual);
    }

    @Test
    public void getUserByUsername() {
        User actualUser = accountManager.getUserByUsername("marmofayezi");
        Assert.assertEquals(marmof,actualUser);
    }

    @Test
    public void isUsernameAvailable(){
        boolean actual = accountManager.isUsernameAvailable("marmofayezi");
        Assert.assertTrue(actual);
    }





}

