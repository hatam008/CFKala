package View.Controllers;

import ModelPackage.Product.NoSuchSellerException;
import ModelPackage.System.exeption.account.UserNotAvailableException;
import ModelPackage.System.exeption.cart.NoSuchAProductInCart;
import ModelPackage.System.exeption.cart.NotEnoughAmountOfProductException;
import ModelPackage.System.exeption.product.NoSuchAProductException;
import View.CacheData;
import View.Main;
import View.PrintModels.CartPM;
import View.PrintModels.InCartPM;
import View.PrintModels.MiniProductPM;
import com.jfoenix.controls.JFXButton;
import controler.CustomerController;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class Cart extends BackAbleController {
    // TODO: 6/20/2020 Fu***** Full Of Problems !!!!!!!!!!!!!!!!!!!!!!! Need A ReProgram
    public JFXButton back;
    public JFXButton minimize;
    public JFXButton close;
    public Label totalPrice;
    public JFXButton purchase;
    public TableColumn<InCartPM, MiniProductPM> product;
    public TableColumn<InCartPM, Integer> price;
    public TableColumn<InCartPM, Integer> afterOff;
    public TableView<InCartPM> tableView;
    public Button goToProduct;
    public Button delete;
    public Button increase;
    public Button decrease;
    public TableColumn<InCartPM, Integer> amount;


    private CustomerController customerController;
    private CacheData cacheData;
    private CartPM cartPM;

    @FXML
    public void initialize() {
        customerController = CustomerController.getInstance();
        cacheData = CacheData.getInstance();
        buttonInitialize();
        setDisabilityOfButtons();
        loadCartPM();
        totalPrice.setText(Long.toString(cartPM.getTotalPrice()));
        loadTable();
    }

    private void buttonInitialize() {
        close.setOnAction(e -> handleClose());
        minimize.setOnAction(e -> Main.minimize());
        back.setOnAction(e -> handleBackButton());
        purchase.setOnAction(e -> handlePurchaseButton());
        delete.setOnAction(e -> handleDeleteButton());
        increase.setOnAction(e -> handleIncreaseDecreaseButton(1));
        decrease.setOnAction(e -> handleIncreaseDecreaseButton(-1));
        goToProduct.setOnAction(e -> handleGoToProductButton());
    }

    private void handleClose() {

    }

    private void setDisabilityOfButtons() {
        goToProduct.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
        increase.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
        decrease.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
        delete.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
    }

    private void handleGoToProductButton() {
        try {
            Main.setRoot("ProductDigest");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleIncreaseDecreaseButton(int change) {
        InCartPM purchase = tableView.getSelectionModel().getSelectedItem();
        try {
            customerController.changeAmount(cacheData.getUsername(), purchase.getProduct().getId(), change);
            tableView.refresh();
        } catch (UserNotAvailableException | NotEnoughAmountOfProductException |
                NoSuchSellerException | NoSuchAProductException | NoSuchAProductInCart e) {
            new OopsAlert().show(e.getMessage());
        }
    }

    private void handleDeleteButton() {
        ObservableList<InCartPM> purchase = tableView.getItems();
        ObservableList<InCartPM> selected = tableView.getSelectionModel().getSelectedItems();
        selected.forEach(purchase::remove);
        // TODO: 6/20/2020
        Notification.show(null, "Items were Deleted Successfully!!!", back.getScene().getWindow(), false);
    }

    private void loadCartPM() {
        try {
            cartPM = customerController.viewCart(cacheData.getUsername());
        } catch (UserNotAvailableException | NoSuchSellerException e) {
            e.printStackTrace();
        }
    }

    private void handleBackButton() {
        try {
            Scene scene = new Scene(Main.loadFXML(back(), backForBackward()));
            Main.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePurchaseButton() {
        try {
            Main.setRoot("Purchase");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTable() {
        product.setCellValueFactory(new PropertyValueFactory<>("product"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        afterOff.setCellValueFactory(new PropertyValueFactory<>("offPrice"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        tableView.setItems(getEverySubCartInformation());
    }

    private ObservableList<InCartPM> getEverySubCartInformation() {
        ObservableList<InCartPM> purchases = FXCollections.observableArrayList();
        purchases.addAll(cartPM.getPurchases());
        return purchases;
    }
}
