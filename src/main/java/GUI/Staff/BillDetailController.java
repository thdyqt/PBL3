package GUI.Staff;

import EntityDTO.Order;
import EntityDTO.OrderDetail;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import org.w3c.dom.Entity;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BillDetailController{

    @FXML
    private ComboBox<String> cbbSearchOption;

    @FXML
    private TableColumn<EntityDTO.OrderDetail, Integer> col_ItemID;

    @FXML
    private TableColumn<EntityDTO.OrderDetail, String> col_ItemName;

    @FXML
    private TableColumn<EntityDTO.OrderDetail, Integer> col_ItemCategoryID;

    @FXML
    private TableColumn<EntityDTO.OrderDetail, Integer> col_ItemQuanity;

    @FXML
    private TableColumn<EntityDTO.OrderDetail, Integer> col_Price;

    @FXML
    private TableColumn<EntityDTO.OrderDetail, Integer> col_TotalPrice;

    @FXML
    private Label label_CustomerID;

    @FXML
    private Label label_OrderID;

    @FXML
    private Label label_ProcessTime;

    @FXML
    private Label label_StaffID;

    @FXML
    private BorderPane mainPane;

    @FXML
    private TableView<EntityDTO.OrderDetail> tbOrderDetail;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField totalAmountBox;

    //objects
    //an active version of the database
    //will update itself when changes are made
    private ObservableList<EntityDTO.OrderDetail> masterData = FXCollections.observableArrayList();
    //for search function
    //work by hiding unrelated data
    private FilteredList<OrderDetail> filteredData;

    @FXML
    private void initialize() {
        setupCombobox();
        setupTable();
        setupSearch();
    }

    private void setupCombobox(){
        cbbSearchOption.getItems().addAll(
            "Chọn tiêu chí tìm kiếm",
            "Tìm kiếm theo ID sản phẩm",
            "Tìm kiếm theo tên sản phẩm",
            "Tìm kiếm theo ID nhóm sản phẩm"
        );
        cbbSearchOption.getSelectionModel().selectFirst();
    }

    private void setupTable(){
        col_ItemID.setCellValueFactory(cellData -> {
            if (cellData.getValue().getProduct() != null) {
                int itemID = cellData.getValue().getProduct().getProductID();
                return new SimpleIntegerProperty(itemID).asObject();
            }
            return new SimpleIntegerProperty(0).asObject();
        });

        col_ItemName.setCellValueFactory(cellData -> {
            if (cellData.getValue().getProduct() != null) {
                String name = cellData.getValue().getProduct().getProductName();
                return new SimpleStringProperty(name);
            }
            return new SimpleStringProperty("Sản phẩm lỗi/Đã xóa");
        });

        col_ItemCategoryID.setCellValueFactory(cellData -> {
            if (cellData.getValue().getProduct() != null) {
                int categoryID = cellData.getValue().getProduct().getCategoryID();
                return new SimpleIntegerProperty(categoryID).asObject();
            }
            return new SimpleIntegerProperty(0).asObject();
        });

        col_ItemQuanity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        col_TotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }

    private void setupSearch(){
        filteredData = new FilteredList<>(masterData, b -> true);

        SortedList<EntityDTO.OrderDetail> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tbOrderDetail.comparatorProperty());
        tbOrderDetail.setItems(sortedData);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(detail -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String keyword = newValue.toLowerCase().trim();
                String searchOption = cbbSearchOption.getValue();

                if (searchOption == null || searchOption.equals("Chưa chọn tiêu chí tìm kiếm")) {
                    return true;
                }

                switch (searchOption) {
                    case "Tìm kiếm theo ID sản phẩm":
                        return String.valueOf(detail.getProduct().getProductID()).contains(keyword);
                    case "Tìm kiếm theo Tên sản phẩm":
                        if (detail.getProduct() != null && detail.getProduct().getProductName() != null) {
                            return detail.getProduct().getProductName().toLowerCase().contains(keyword);
                        }
                        return false;
                    default:
                        return true;
                }
            });
        });

        cbbSearchOption.valueProperty().addListener((obs, oldVal, newVal) -> {
            String currentText = txtSearch.getText();
            txtSearch.setText("");
            txtSearch.setText(currentText);
        });
    }

    public void setOrderDetails(EntityDTO.Order order) {
        label_OrderID.setText("ID hóa đơn: " + String.valueOf(order.getId()));
        label_ProcessTime.setText(order.getProcess_time().toString());

        if (order.getStaff() != null) {
            label_StaffID.setText("ID nhân viên: " + order.getStaff().getId());
        }
        if (order.getCustomer() != null) {
            label_CustomerID.setText("ID khách hàng: "+String.valueOf(order.getCustomer().getId()));
        } else {
            label_CustomerID.setText("Khách vãng lai");
        }

        List<OrderDetail> detailsList = BusinessBLL.OrderDetailBusiness.getDetailsByOrderId_BLL(order.getId());

        masterData.setAll(detailsList);
    }

}
