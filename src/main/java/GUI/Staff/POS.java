package GUI.Staff;

import EntityDTO.Category;
import EntityDTO.OrderDetail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

public class POS {
    @FXML
    private Button btnAddCustomer;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnPayment;

    @FXML
    private ComboBox<Category> cbbCategory;

    @FXML
    private ComboBox<EntityDTO.PromoCode> cbbDiscount;

    @FXML
    private TableView<OrderDetail> tableCart;

    @FXML
    private TableColumn<OrderDetail, Integer> colSTT;

    @FXML
    private TableColumn<OrderDetail, String> colProductName;

    @FXML
    private TableColumn<OrderDetail, Integer> colQuantity;

    @FXML
    private TableColumn<OrderDetail, Integer> colPrice;

    @FXML
    private TableColumn<OrderDetail, Integer> colTotal;

    @FXML
    private FlowPane flowPaneProducts;

    @FXML
    private Label lblCustomerName;

    @FXML
    private Label lblSubtotal;

    @FXML
    private Label lblTotalPay;

    @FXML
    private Label lblTotalQuantity;

    @FXML
    private TextField txtCustomerPhone;

    @FXML
    private TextField txtSearchProduct;

    @FXML
    void handleAddCustomer(ActionEvent event) {

    }

    @FXML
    void handleCancelOrder(ActionEvent event) {

    }

    @FXML
    void handlePayment(ActionEvent event) {

    }

}
