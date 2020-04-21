package ModelPackage.System;

import ModelPackage.Product.Comment;
import ModelPackage.Product.CommentStatus;
import ModelPackage.Product.Product;
import ModelPackage.Users.Request;
import ModelPackage.Users.RequestType;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class RequestManagerTest {
    private RequestManager requestManager;
    private Request request;

    {
        requestManager = RequestManager.getInstance();
        request = new Request("asghar",RequestType.CREATE_PRODUCT,
                "adsfasdf asdf",new Product());

        requestManager.clear();
        requestManager.addRequest(request);
    }

    @Test
    public void getInstanceTest(){
        RequestManager test = RequestManager.getInstance();

        Assert.assertEquals(requestManager,test);
    }

    @Test
    public void addRequestTest(){
        requestManager.clear();
        requestManager.addRequest(request);
        int actual = requestManager.getRequests().size();
        Assert.assertEquals(1,actual);
    }

    @Test
    public void findRequestById(){
        Request request = requestManager.findRequestById(this.request.getRequestId());
        Assert.assertEquals(this.request,request);
    }

    @Test
    public void findRequestByIdNull(){
        Request request = requestManager.findRequestById("RQ;adkfljasdjflsdjkfj;askldjf");
        Assert.assertNull(request);
    }

    @Test
    public void acceptCreateProductTest(){
        requestManager.accept(request.getRequestId());
        boolean successful = ProductManager.getInstance().doesThisProductExist(request.getProduct().getProductId());
        Assert.assertTrue(successful);
    }

    @Test
    public void acceptEditProductTest(){
        Product existedProduct = new Product();
        ProductManager.getInstance().addProductToList(existedProduct);
        Product changed = new Product();
        changed.setProductId(existedProduct.getProductId());
        changed.setView(20);
        Request editRequest = new Request("asghar",RequestType.CHANGE_PRODUCT,"asdf",changed);
        requestManager.addRequest(editRequest);
        requestManager.accept(editRequest.getRequestId());
        int actulView = ProductManager.getInstance().findProductById(existedProduct.getProductId()).getView();

        Assert.assertEquals(20,actulView);
    }

    @Test
    public void acceptAssignCommentTest(){
        Product product = new Product();
        product.setAllComments(new ArrayList<>());
        ProductManager.getInstance().addProductToList(product);
        Comment comment = new Comment(product.getProductId(),"asdf","adf","asdfsad saf", CommentStatus.UNDER_VERIFICATION,true);
        Request request1 = new Request("asghar",RequestType.ASSIGN_COMMENT,"adf",comment);
        requestManager.addRequest(request1);
        requestManager.accept(request1.getRequestId());

        Assert.assertEquals(comment.getId(),product.getAllComments().get(0).getId());
    }

    @Test
    public void declineTest(){
        requestManager.decline(request.getRequestId());
        Request request1 = requestManager.findRequestById(request.getRequestId());

        Assert.assertNull(request1);
    }
}