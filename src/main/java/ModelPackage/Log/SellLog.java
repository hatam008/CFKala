package ModelPackage.Log;

import ModelPackage.Product.Product;
import ModelPackage.Users.User;
import lombok.*;

@Setter @Getter @RequiredArgsConstructor
public class SellLog extends Log {
    @NonNull private Product product;
    @NonNull private int moneyGotten;
    @NonNull private int discount;
    @NonNull private User buyer;
}
