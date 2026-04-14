package GUI.Staff;

import BusinessBLL.CategoryBusiness;
import BusinessBLL.ProductBusiness;
import EntityDTO.Category;
import EntityDTO.OrderDetail;
import EntityDTO.Product;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class POS implements Initializable {
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

    private ObservableList<OrderDetail> cartList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colSTT.setCellFactory(column -> new TableCell<EntityDTO.OrderDetail, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        colProductName.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getProduct().getProductName());
        });

        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        tableCart.setItems(cartList);
        loadCategories();
        loadProducts();
    }

    private void loadCategories() {
        List<Category> listCat = CategoryBusiness.getAllCategories();
        cbbCategory.setItems(FXCollections.observableArrayList(listCat));
    }

    private void loadProducts() {
        List<Product> listPro = ProductBusiness.getAllProducts();

        flowPaneProducts.getChildren().clear();

        for (Product sp : listPro) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Staff/ProductCard.fxml"));
                VBox card = loader.load();

                ProductCardController controller = loader.getController();
                controller.setData(sp);

                card.setOnMouseClicked(event -> {
                    //addToCart(sp);
                });

                flowPaneProducts.getChildren().add(card);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


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
