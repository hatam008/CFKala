package ModelPackage.System;

import ModelPackage.Off.DiscountCode;
import ModelPackage.System.exeption.discount.*;
import ModelPackage.Users.Cart;
import ModelPackage.Users.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class DiscountManagerTest {
    private DiscountManager discountManager;
    private DiscountCode discountCode;
    private DiscountCode discountCode1;
    private DiscountCode discountCode2;
    private User ali;
    private User reza;
    private HashMap<User, Integer> users;
    {
        discountManager = DiscountManager.getInstance();
        ali = new User("ali008", "124345", "Ali",
                "Alavi", "Ali@gmail.com", "092000000", new Cart());
        reza = new User("reza12", "125423", "Reza",
                "Rezaei", "reza@gmail.com", "0913232343", new Cart());
        users = new HashMap<>();
        users.put(ali, 2);
        users.put(reza, 1);
        discountCode = new DiscountCode(new Date(), new Date(2020, Calendar.MAY, 1), 10, 10);
        discountCode.setCode("Dis#12");
        discountCode.getUsers().putAll(users);
        discountCode1 = new DiscountCode(new Date(2020, Calendar.MARCH, 1), new Date(2020, Calendar.MARCH, 4), 10, 10);
        discountCode1.setCode("Dis#13");
        discountCode1.getUsers().putAll(users);
        discountCode2 = new DiscountCode(new Date(2019, Calendar.MARCH, 3), new Date(2021, Calendar.MARCH, 1), 10, 10);
        discountCode2.setCode("Dis#14");
        discountCode2.getUsers().putAll(users);
        discountManager.getDiscountCodes().add(discountCode);
        discountManager.getDiscountCodes().add(discountCode1);
        discountManager.getDiscountCodes().add(discountCode2);
    }
    @Test
    public void getInstanceTest() {
        DiscountManager test = DiscountManager.getInstance();
        Assert.assertEquals(discountManager, test);
    }
    @Test
    public void getDiscountByCodeTest() throws NoSuchADiscountCodeException {
        DiscountCode actual = discountManager.getDiscountByCode("Dis#12");
        Assert.assertEquals(discountCode, actual);
    }
    @Test(expected = NoSuchADiscountCodeException.class)
    public void getDiscountByCodeNotFoundExcTest() throws Exception{
        discountManager.getDiscountByCode("Dis#10");
    }
    @Test
    public void isDiscountAvailableTest() throws NoSuchADiscountCodeException {
        boolean successful = discountManager.isDiscountAvailable("Dis#13");
        Assert.assertFalse(successful);
        successful = discountManager.isDiscountAvailable("Dis#12");
        Assert.assertTrue(successful);
    }
    @Test(expected = NoSuchADiscountCodeException.class)
    public void isDiscountAvailableNotFoundExcTest() throws Exception{
        discountManager.isDiscountAvailable("Dis#10");
    }
    @Test
    public void removeDiscountTest() throws NoSuchADiscountCodeException {
        discountManager.removeDiscount("Dis#12");
        DiscountCode discountCode = discountManager.getDiscountByCode("Dis#12");
        Assert.assertNull(discountCode);
    }
    @Test(expected = NoSuchADiscountCodeException.class)
    public void removeDiscountNotFoundExcTest() throws Exception{
        discountManager.removeDiscount("Dis#10");
    }
    @Test(expected = NoSuchADiscountCodeException.class)
    public void editDiscountStartingDateNotFoundExcTest() throws Exception{
        discountManager.editDiscountStartingDate("Dis#10", new Date());
    }
    @Test(expected = NoSuchADiscountCodeException.class)
    public void editDiscountEndingDateNotFoundExcTest() throws Exception {
        discountManager.editDiscountEndingDate("Dis#10", new Date(2020, Calendar.MARCH, 12));
    }
    @Test(expected = NoSuchADiscountCodeException.class)
    public void editDiscountOffPercentageNotFoundExcTest() throws Exception {
        discountManager.editDiscountOffPercentage("Dis#10", 12);
    }
    @Test(expected = NoSuchADiscountCodeException.class)
    public void editDiscountMaxDiscountNotFoundExcTest() throws Exception{
        discountManager.editDiscountMaxDiscount("Dis#10", 210000000);
    }
    @Test(expected = NoSuchADiscountCodeException.class)
    public void addUserToDiscountUsersNotFoundExcTest() throws Exception {
        discountManager.addUserToDiscountCodeUsers("Dis#10", ali, 4);
    }
    @Test(expected = NoSuchADiscountCodeException.class)
    public void removeUserFromDiscountUsersNotFoundExcTest() throws Exception {
        discountManager.removeUserFromDiscountCodeUsers("Dis#10", ali);
    }
    @Test
    public void editDiscountStartingDateTest() throws NoSuchADiscountCodeException,
            StartingDateIsAfterEndingDate {
        Date newDate = new Date(2010, Calendar.MARCH, 15);
        discountManager.editDiscountStartingDate("Dis#13", newDate);
        Date actual = discountManager.getDiscountByCode("Dis#13").getStartTime();
        Assert.assertEquals(newDate, actual);
    }
    @Test(expected = StartingDateIsAfterEndingDate.class)
    public void editDiscountStartingDateStartingAfterEndingTest() throws Exception{
        discountManager.editDiscountStartingDate("Dis#12", new Date(2022, Calendar.FEBRUARY, 1));
    }
    @Test
    public void editDiscountEndingDateTest()
            throws NoSuchADiscountCodeException, StartingDateIsAfterEndingDate {
        Date newDate = new Date(2020, Calendar.MARCH, 5);
        discountManager.editDiscountEndingDate("Dis#13", newDate);
        Date actual = discountManager.getDiscountByCode("Dis#13").getEndTime();
        Assert.assertEquals(newDate, actual);
    }
    @Test(expected = StartingDateIsAfterEndingDate.class)
    public void editDiscountEndingDateBeforeStartingExcTest() throws Exception{
        discountManager.editDiscountEndingDate("Dis#14", new Date(2018, Calendar.FEBRUARY, 12));
    }
    @Test
    public void editDiscountOffPercentage()
            throws NoSuchADiscountCodeException, NotValidPercentageException {
        int newPercentage = 20;
        discountManager.editDiscountOffPercentage("Dis#13", newPercentage);
        int actual = discountManager.getDiscountByCode("Dis#13").getOffPercentage();
        Assert.assertEquals(newPercentage, actual);
    }
    @Test(expected = NotValidPercentageException.class)
    public void editDiscountOffPercentageNotValidExcTest() throws NotValidPercentageException, NoSuchADiscountCodeException {
        discountManager.editDiscountOffPercentage("Dis#14", -12);
    }
    @Test
    public void editDiscountMaxDiscountTest()
            throws NoSuchADiscountCodeException, NegativeMaxDiscountException {
        long newMaxDiscount = 1000;
        discountManager.editDiscountMaxDiscount("Dis#14", newMaxDiscount);
        long actual = discountManager.getDiscountByCode("Dis#14").getMaxDiscount();
        Assert.assertEquals(newMaxDiscount, actual);
    }
    @Test(expected = NegativeMaxDiscountException.class)
    public void editDiscountMaxDiscountNegativeExcTest()
            throws NegativeMaxDiscountException, NoSuchADiscountCodeException {
        discountManager.editDiscountMaxDiscount("Dis#14", -1221);
    }
    @Test
    public void addUserToDiscountCodeUsersTest()
            throws NoSuchADiscountCodeException, UserExistedInDiscountCodeException {
        User javad = new User("java", "12@1$", "javad",
                "javadi", "javad@gmail.com", "0913345256", new Cart());
        discountManager.addUserToDiscountCodeUsers("Dis#13", javad, 4);
        boolean successful = discountManager.getDiscountByCode("Dis#13").getUsers().containsKey(javad);
        Assert.assertTrue(successful);
    }
    @Test(expected = UserExistedInDiscountCodeException.class)
    public void addUserToDiscountCodeExistedExcTest() throws Exception{
        discountManager.addUserToDiscountCodeUsers("Dis#14", ali, 12);
    }
    @Test
    public void removeUserFromDiscountCodeUsersTest()
            throws NoSuchADiscountCodeException, UserNotExistedInDiscountCodeException {
        User taghi = new User("tagh", "12%$#", "Taghi",
                "Taghavi", "taghi@gmail.com", "0912235433", new Cart());
        User naghi = new User("nagh", "12#2", "Naghi",
                "Naghavi", "naghi@gmail.com", "0912874556", new Cart());
        discountManager.getDiscountByCode("Dis#14").getUsers().put(taghi, 13);
        discountManager.getDiscountByCode("Dis#14").getUsers().put(naghi, 3);
        discountManager.removeUserFromDiscountCodeUsers("Dis#14", taghi);
        boolean successful = discountManager.getDiscountByCode("Dis#14").getUsers().containsKey(taghi);
        Assert.assertFalse(successful);
    }
    @Test(expected = UserNotExistedInDiscountCodeException.class)
    public void removeUserFromDiscountCodeNotFoundExcTest() throws Exception{
        User mohammad = new User("mohammad12", "12542233", "Mohammad",
                "Mohammadi", "mohammad@gmail.com", "0913235465", new Cart());
        discountManager.removeUserFromDiscountCodeUsers("Dis#14", mohammad);
    }
    @Test
    public void showAllDiscountCodes() {
        List<DiscountCode> expectedAllDiscountCodes = discountManager.getDiscountCodes();
        List<DiscountCode> actualAllDiscountCodes = discountManager.showAllDiscountCodes();
        Assert.assertEquals(actualAllDiscountCodes, expectedAllDiscountCodes);
    }
    @Test
    public void showDiscountCodeTest() throws Exception {
        DiscountCode expectedDiscountCode = discountManager.getDiscountCodes().get(discountManager.getDiscountCodes().size() - 1);
        DiscountCode actualDiscountCode = discountManager.showDiscountCode("Dis#14");
        Assert.assertEquals(expectedDiscountCode, actualDiscountCode);
    }
    @Test(expected = NoSuchADiscountCodeException.class)
    public void showDiscountCodeNotFoundExcTest() throws Exception{
        discountManager.showDiscountCode("Dis#10");
    }
    @Test
    public void createDiscountCode() throws Exception{
        Date startDate = new Date(2020, Calendar.FEBRUARY, 1);
        Date endDate = new Date(2020, Calendar.JUNE, 2);
        DiscountCode expectedDiscountCode = new DiscountCode(startDate, endDate, 12, 1000);
        expectedDiscountCode.setCode("Dis@15");
        discountManager.createDiscountCode(startDate, endDate, 12, 1000);
        discountManager.getDiscountCodes().get(discountManager.getDiscountCodes().size() - 1).setCode("Dis@15");
        DiscountCode actualDiscountCode = discountManager.getDiscountCodes().get(discountManager.getDiscountCodes().size() - 1);
        int successful = Comparator.comparing(DiscountCode::getCode).
                thenComparing(DiscountCode::getStartTime).thenComparing(DiscountCode::getEndTime).
                thenComparing(DiscountCode::getOffPercentage).thenComparing(DiscountCode::getMaxDiscount)
                .compare(expectedDiscountCode, actualDiscountCode);
        Assert.assertEquals(0, successful);
    }
    @Test(expected = StartingDateIsAfterEndingDate.class)
        public void createDiscountCodeStartingAfterEndingExcTest() throws Exception{
        discountManager.createDiscountCode(new Date(2022, Calendar.MARCH, 1), new Date(), 12, 122220);
    }
    @Test(expected = NotValidPercentageException.class)
    public void createDiscountCodeInValidPercentageExcTest() throws Exception{
        discountManager.createDiscountCode(new Date(), new Date(2022, Calendar.MARCH, 12), 200, 12);
    }
    @Test
    public void useADiscountTest() throws Exception{
        discountManager.useADiscount(ali, "Dis#12");
        int actual = discountManager.getDiscountByCode("Dis#12").getUsers().get(ali);
        Assert.assertEquals(1, actual);
    }
    @Test(expected = UserNotExistedInDiscountCodeException.class)
    public void useADiscountCodeUserNotHaveChoiceExcTest() throws Exception{
        discountManager.useADiscount(reza, "Dis#13");
        discountManager.useADiscount(reza, "Dis#13");
    }
    @Test(expected = NoSuchADiscountCodeException.class)
    public void useADiscountNotFoundExcTest() throws Exception{
        discountManager.useADiscount(ali, "Dis#10");
    }
    @Test(expected = UserNotExistedInDiscountCodeException.class)
    public void useADiscountCodeNotFoundUserExcTest() throws Exception{
        User mohammad = new User("mohammad12", "12542233", "Mohammad",
                "Mohammadi", "mohammad@gmail.com", "0913235465", new Cart());
        discountManager.useADiscount(mohammad, "Dis#12");
    }
}