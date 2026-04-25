package GUI.Staff;


import EntityDTO.Order;
import Util.Others;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class BillManagementForm{
    @FXML
    private Button buttonOrderDetail;

    @FXML
    private Button buttonOrderReceipt;

    @FXML
    private ComboBox<String> cbbSearchOption;

    @FXML
    private TableColumn<EntityDTO.Order, String> colProcessStaffName;

    @FXML
    private TableColumn<EntityDTO.Order, java.time.LocalDateTime> colProcessTime;

    @FXML
    private TableColumn<EntityDTO.Order, String> col_CustomerName;

    @FXML
    private TableColumn<EntityDTO.Order, Integer> col_OrderID;

    @FXML
    private TableColumn<EntityDTO.Order, String> col_PhoneCustomer;

    @FXML
    private BorderPane mainPane;

    @FXML
    private TableView<EntityDTO.Order> tbOrder;

    @FXML
    private TextField txtSearch;

    private ObservableList<EntityDTO.Order> masterData = FXCollections.observableArrayList();
    private FilteredList<EntityDTO.Order> filteredData;

    //methods
    @FXML
    private void initialize() {
        setupCombobox();
        setupTable();
        loadTable();
        search();

        setupButtons();
    }

    private void setupButtons() {
        Others.playButtonAnimation(buttonOrderDetail);
        Others.playHoverAnimation(buttonOrderDetail);
        Others.playButtonAnimation(buttonOrderReceipt);
        Others.playHoverAnimation(buttonOrderReceipt);


        buttonOrderDetail.setOnAction(event -> {
            handleOrderDetail();
        });

        buttonOrderReceipt.setOnAction(actionEvent -> {
            handleOrderReceipt();
        });
    }

    private void setupCombobox(){
        ObservableList<String> searchOptions = FXCollections.observableArrayList(
                "Chọn tiêu chí tìm kiếm",
                "Tìm kiếm theo ID order",
                "Tìm kiếm theo ID nhân viên thực hiện",
                "Tìm kiếm theo SĐT khách hàng"
        );

        cbbSearchOption.setItems(searchOptions);
        cbbSearchOption.getSelectionModel().selectFirst();

        cbbSearchOption.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtSearch.clear();

                switch (newValue) {
                    case "Chọn tiêu chí tìm kiếm":
                        txtSearch.setPromptText("Chưa chọn tiêu chí tìm kiếm");
                        break;
                    case "Tìm kiếm theo ID order":
                        txtSearch.setPromptText("Nhập ID Đơn hàng");
                        break;
                    case "Tìm kiếm theo Tên nhân viên thực hiện":
                        txtSearch.setPromptText("Nhập Tên Nhân viên");
                        break;
                    case "Tìm kiếm theo SĐT khách hàng":
                        txtSearch.setPromptText("Nhập SĐT Khách hàng");
                        break;
                    default:
                        txtSearch.setPromptText("\uD83D\uDD0D Tìm kiếm đơn hàng...");
                }
            }
        });
    }

    private void setupTable() {
        col_OrderID.setCellValueFactory(new PropertyValueFactory<>("id"));

        colProcessTime.setCellValueFactory(new PropertyValueFactory<>("orderTime"));
        colProcessTime.setCellFactory(column -> new javafx.scene.control.TableCell<EntityDTO.Order, java.time.LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            @Override
            protected void updateItem(java.time.LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        colProcessStaffName.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStaff() != null) {
                return new SimpleStringProperty(cellData.getValue().getStaff().getName());
            }
            return new SimpleStringProperty("");
        });

        col_CustomerName.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCustomer() != null) {
                return new SimpleStringProperty(cellData.getValue().getCustomer().getName());
            }
            return new SimpleStringProperty("Khách lẻ");
        });

        col_PhoneCustomer.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCustomer() != null) {
                return new SimpleStringProperty(cellData.getValue().getCustomer().getPhone());
            }
            return new SimpleStringProperty("");
        });

        col_OrderID.setStyle("-fx-alignment: CENTER;");
        col_CustomerName.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-text-fill: #0F172A; -fx-padding: 0 0 0 15;");
        col_PhoneCustomer.setStyle("-fx-alignment: CENTER;");
        colProcessStaffName.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-text-fill: #0F172A; -fx-padding: 0 0 0 15;");
        colProcessTime.setStyle("-fx-alignment: CENTER;");
    }

    private void search(){
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(order -> {
                if (newValue == null || newValue.trim().isEmpty()){
                    return true;
                }

                String keyword = newValue.toLowerCase().trim();
                String searchOption = cbbSearchOption.getValue();

                if (searchOption == null){
                    return true;
                }

                switch (searchOption){
                    case "Chưa chọn tiêu chí tìm kiếm":
                        return true;
                    case "Tìm kiếm theo ID order":
                        return String.valueOf(order.getId()).contains(keyword);
                    case "Tìm kiếm theo Tên nhân viên thực hiện":
                        if (order.getStaff() != null){
                            return String.valueOf(order.getStaff().getName()).contains(keyword);
                        }
                        return false;
                    case "Tìm kiếm theo SĐT khách hàng":
                        if (order.getCustomer() != null && order.getCustomer().getPhone() != null){
                            return String.valueOf(order.getCustomer().getPhone()).contains(keyword);
                        }
                        return false;
                    default:
                        return false;
                }

            });

            });
            cbbSearchOption.valueProperty().addListener((obs, oldVal, newVal) -> {
            String currentText = txtSearch.getText();
            txtSearch.setText("");
            txtSearch.setText(currentText);
        });
    }

    private void handleOrderDetail(){
        EntityDTO.Order selectedOrder = tbOrder.getSelectionModel().getSelectedItem();

        if (selectedOrder == null){
            Others.showAlert(mainPane, "Vui lòng chọn hóa đơn!", true);
            return;
        }

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Staff/BillDetail.fxml"));
            Parent root = loader.load();

            BillDetailController detailController = loader.getController();
            detailController.setOrderDetails(selectedOrder);

            Stage stage = new Stage();
            stage.setTitle("Thông tin hóa đơn #" + selectedOrder.getId());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleOrderReceipt(){
        EntityDTO.Order selectedOrder = tbOrder.getSelectionModel().getSelectedItem();

        if (selectedOrder == null){
            Others.showAlert(mainPane, "Vui lòng chọn hóa đơn!", true);
            return;
        }

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Staff/BillReceipt.fxml"));
            Parent root = loader.load();

            BillReceiptController receiptController = loader.getController();
            receiptController.setData(selectedOrder);

            Stage stage = new Stage();
            stage.setTitle("Hóa đơn #" + selectedOrder.getId());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTable(){
        List<Order> list = BusinessBLL.OrderBusiness.getFilteredOrders(Order.OrderType.Offline);
        masterData.setAll(list);
        filteredData = new FilteredList<>(masterData, temp -> true);

        SortedList<EntityDTO.Order> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tbOrder.comparatorProperty());

        tbOrder.setItems(sortedData);

        Others.animateTableRows(tbOrder);
    }
}
