package ModelPackage.System;

import ModelPackage.Off.DiscountCode;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class DiscountManager {
    private List<DiscountCode> discountCodes;

    private static DiscountManager discountManager = new DiscountManager();

    public static DiscountManager getInstance() {
        return discountManager;
    }

    private DiscountManager() {
        this.discountCodes = new ArrayList<>();
    }

    public DiscountCode getDiscountByCode(String code) {
        for (DiscountCode discountCode : discountCodes) {
            if (code.equals(discountCode.getCode()))
                return discountCode;
        }
        return null;
    }

    public boolean isDiscountAvailable(String code) {
        DiscountCode discountCode = getDiscountByCode(code);
        Date date = new Date();
        Date startDate = discountCode.getStartTime();
        Date endDate = discountCode.getEndTime();
        return !date.before(startDate) && !date.after(endDate);
    }

    public void removeDiscount(String code) {
        DiscountCode discountCode = getDiscountByCode(code);
        discountCodes.remove(discountCode);
    }

    public void editDiscountStartingDate(String code, Date newStartingDate) {
        DiscountCode discountCode = getDiscountByCode(code);
        discountCode.setStartTime(newStartingDate);
    }

    public void editDiscountEndingDate(String code, Date newEndingDate) {
        DiscountCode discountCode = getDiscountByCode(code);
        discountCode.setEndTime(newEndingDate);
    }

    public void editDiscountOffPercentage(String code, int newPercentage) {
        DiscountCode discountCode = getDiscountByCode(code);
        discountCode.setOffPercentage(newPercentage);
    }

    public void editDiscountMaxDiscount(String code, long newMaxDiscount) {
        DiscountCode discountCode = getDiscountByCode(code);
        discountCode.setMaxDiscount(newMaxDiscount);
    }
}
