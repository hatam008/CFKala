package ModelPackage.System;

import ModelPackage.Off.Off;
import ModelPackage.Product.NoSuchSellerException;
import ModelPackage.Product.Product;
import ModelPackage.Product.SellPackage;
import ModelPackage.System.editPackage.OffChangeAttributes;
import ModelPackage.Off.OffStatus;
import ModelPackage.System.database.DBManager;
import ModelPackage.System.exeption.off.InvalidTimes;
import ModelPackage.System.exeption.off.NoSuchAOffException;
import ModelPackage.System.exeption.off.ThisOffDoesNotBelongssToYouException;
import ModelPackage.System.exeption.product.NoSuchAProductException;
import ModelPackage.Users.Request;
import ModelPackage.Users.RequestType;
import ModelPackage.Users.Seller;
import ModelPackage.Users.User;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class OffManager {
    private static OffManager offManager = new OffManager();

    public static OffManager getInstance() {
        return offManager;
    }

    public void createOff(Seller seller, Date[] dates,int percentage) throws InvalidTimes {
        if (!dates[0].before(dates[1])) throw new InvalidTimes();
        Off off = new Off();
        off.setSeller(seller);
        off.setOffPercentage(percentage);
        off.setOffStatus(OffStatus.CREATION);
        off.setStartTime(dates[0]);
        off.setEndTime(dates[1]);
        DBManager.save(off);
        String strRequest = String.format("%s requested to create an off with percentage %d",seller.getUsername(),percentage);
        Request request = new Request(seller.getUsername(), RequestType.CREATE_OFF,strRequest,off);
        RequestManager.getInstance().addRequest(request);
        seller.addRequest(request);
        DBManager.save(seller);
    }

    public Off findOffById(int id) throws NoSuchAOffException {
        Off off = DBManager.load(Off.class,id);
        if (off == null) throw new NoSuchAOffException(id);
        return off;
    }

    public void editOff(OffChangeAttributes changeAttributes,String editor) throws NoSuchAOffException, ThisOffDoesNotBelongssToYouException {
        Off off = findOffById(changeAttributes.getSourceId());
        checkIfThisSellerCreatedTheOff(off,editor);
        off.setOffStatus(OffStatus.EDIT);
        String strRequest = String.format("%s requested to edit an off with id %d",editor,off.getOffId());
        Request request = new Request(editor, RequestType.EDIT_OFF,strRequest,changeAttributes);
        RequestManager.getInstance().addRequest(request);
        Seller seller = DBManager.load(Seller.class, editor);
        if (seller != null) {
            seller.addRequest(request);
            DBManager.save(seller);
        }
    }

    public void deleteOff(int id,String remover) throws NoSuchAOffException, ThisOffDoesNotBelongssToYouException {
        Off off = findOffById(id);
        checkIfThisSellerCreatedTheOff(off,remover);
        deleteOff(off);
    }

    private void deleteOff(Product product, Seller remover) {
        try {
            SellPackage sellPackage = product.findPackageBySeller(remover);
            sellPackage.setOnOff(false);
            sellPackage.setOff(null);
            DBManager.save(sellPackage);
        } catch (NoSuchSellerException e) {
            e.printStackTrace();
        }
    }

    void deleteOff(Off off) {
        off.getProducts().forEach(product -> deleteOff(product, off.getSeller()));
        DBManager.delete(off);
    }

    public void checkIfThisSellerCreatedTheOff(Off off,String viewer) throws ThisOffDoesNotBelongssToYouException {
        if (!off.getSeller().getUsername().equals(viewer))throw new ThisOffDoesNotBelongssToYouException();
    }
}