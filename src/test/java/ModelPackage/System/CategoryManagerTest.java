package ModelPackage.System;

import ModelPackage.Product.Category;
import ModelPackage.System.exeption.category.NoSuchACategoryException;
import ModelPackage.System.exeption.category.RepeatedNameInParentCategoryExeption;
import ModelPackage.System.exeption.product.NoSuchAProductException;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.junit.Assert;
import org.junit.Test;

public class CategoryManagerTest {
    private CategoryManager categoryManager;
    private Category main;
    private Category category1;
    private Category category2;
    private Category category3;

    {
        categoryManager = CategoryManager.getInstance();
        main = new Category("Main",null);
        main.setParentId("MNCTCFKala");
        category1 = new Category("cloth",main.getId());
        categoryManager.addToBase(category1,main);
        category2 = new Category("mobile",main.getId());
        categoryManager.addToBase(category2,main);
        category3 = new Category("men",category1.getId());
        categoryManager.addToBase(category3,category1);

        categoryManager.clear();
        categoryManager.add(category1);
        categoryManager.add(category2);
        categoryManager.addM(category1);
        categoryManager.addM(category2);
        categoryManager.add(category3);
    }

    @Test
    public void getInstanceTest(){
        CategoryManager temp = CategoryManager.getInstance();
        Assert.assertEquals(categoryManager,temp);
    }

    @Test
    public void createCategoryTest() {
        try {
            categoryManager.createCategory("women",category1.getId());
        }catch (Exception e){

        }
        int countOfSubCats = category1.getSubCategories().size();
        Assert.assertEquals(2,countOfSubCats);
    }

    @Test
    public void createCategoryRepNameExcTest(){
        Assert.assertThrows(RepeatedNameInParentCategoryExeption.class,() -> {
            categoryManager.createCategory("men",category1.getId());
        });
    }

    @Test
    public void createCategoryNoCatExcTest(){
        Assert.assertThrows(NoSuchACategoryException.class,() -> {
            categoryManager.createCategory("men","asdf");
        });
    }

    /*@Test
    public void addProductToCategoryTest() throws Exception{
        categoryManager.addProductToCategory("abc",category1.getId());

        int countOfProducts = category1.getAllProductInThis().size();
        Assert.assertEquals(1,countOfProducts);
    }*/
}

