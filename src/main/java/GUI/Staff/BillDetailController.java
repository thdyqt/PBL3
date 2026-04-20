package GUI.Staff;

import EntityDTO.OrderDetail;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

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

        col_Price.setCellValueFactory(new PropertyValueFactory<>("price"));
        col_Price.setCellFactory(column -> new javafx.scene.control.TableCell<EntityDTO.OrderDetail, Integer>() {
            private final java.text.DecimalFormat formatter;
            {
                java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
                symbols.setGroupingSeparator('.');
                formatter = new java.text.DecimalFormat("#,###", symbols);
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item) + "đ");
                }
            }
        });

        col_TotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        col_TotalPrice.setCellFactory(column -> new javafx.scene.control.TableCell<EntityDTO.OrderDetail, Integer>() {
            private final java.text.DecimalFormat formatter;
            {
                java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
                symbols.setGroupingSeparator('.');
                formatter = new java.text.DecimalFormat("#,###", symbols);
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item) + "đ");
                }
            }
        });


        col_ItemID.setStyle("-fx-alignment: CENTER;");
        col_ItemName.setStyle("-fx-alignment: CENTER;");
        col_ItemCategoryID.setStyle("-fx-alignment: CENTER;");
        col_ItemQuanity.setStyle("-fx-alignment: CENTER;");
        col_Price.setStyle("-fx-alignment: CENTER;");
        col_TotalPrice.setStyle("-fx-alignment: CENTER;");
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

    //set up the 4 information buttons at the top
    public void setOrderDetails(EntityDTO.Order order) {
        label_OrderID.setText("ID hóa đơn: " + String.valueOf(order.getId()));
        label_ProcessTime.setText(order.getOrderTime().toString());

        if (order.getStaff() != null) {
            label_StaffID.setText("ID nhân viên: " + order.getStaff().getId());
        }
        if (order.getCustomer() != null) {
            label_CustomerID.setText("ID khách hàng: " + String.valueOf(order.getCustomer().getId()));
        } else {
            label_CustomerID.setText("Khách vãng lai");
        }

        List<OrderDetail> detailsList = BusinessBLL.OrderDetailBusiness.getDetailsByOrderId_BLL(order.getId());

        masterData.setAll(detailsList);

        int grandTotal = 0;
        for (OrderDetail detail : detailsList) {
            grandTotal += detail.getTotalPrice();
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);


        totalAmountBox.setText(formatter.format(grandTotal) + "đ");
    }

}
