package GUI;

import Data.CustomerData;
import Entity.Customer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class CustomerManagementForm {

    @FXML
    private Button btnSort;

    @FXML
    private ComboBox<String> cbbSort; // Nên định nghĩa kiểu dữ liệu cho ComboBox

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<Customer> tableCustomer;

    // Khai báo các cột tương ứng với giao diện
    @FXML
    private TableColumn<Customer, Integer> colId;

    @FXML
    private TableColumn<Customer, String> colName;

    @FXML
    private TableColumn<Customer, String> colPhone;

    @FXML
    private TableColumn<Customer, Integer> colPoint;

    @FXML
    private TableColumn<Customer, Customer.rank> colRank;

    @FXML
    public void initialize() {
        // 1. Ánh xạ dữ liệu cho các cột cơ bản
        colId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        colPoint.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPoint()));

        // 2. Cấu hình cột hiển thị Hạng (Badge màu sắc)
        setupRankColumn();

        // 3. Tải dữ liệu từ Database lên bảng
        loadData();
    }

    // Hàm tải dữ liệu từ Database
    private void loadData() {
        List<Customer> listCustomers = CustomerData.getAllCustomers();
        // Chuyển List thường thành ObservableList của JavaFX để bảng có thể tự động cập nhật
        ObservableList<Customer> observableList = FXCollections.observableArrayList(listCustomers);
        tableCustomer.setItems(observableList);
    }

    private void setupRankColumn() {
        colRank.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCustomer_rank())
        );

        colRank.setCellFactory(column -> {
            return new TableCell<Customer, Customer.rank>() {
                @Override
                protected void updateItem(Customer.rank item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Label badge = new Label();
                        badge.setStyle("-fx-padding: 3 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 12px;");

                        switch (item) {
                            case Bronze:
                                badge.setText("Đồng");
                                badge.setStyle(badge.getStyle() + "-fx-background-color: #FFEDD5; -fx-text-fill: #9A3412;");
                                break;
                            case Silver:
                                badge.setText("Bạc");
                                badge.setStyle(badge.getStyle() + "-fx-background-color: #F1F5F9; -fx-text-fill: #475569;");
                                break;
                            case Gold:
                                badge.setText("Vàng");
                                badge.setStyle(badge.getStyle() + "-fx-background-color: #FEF08A; -fx-text-fill: #854D0E;");
                                break;
                            case Platinum:
                                badge.setText("Bạch Kim");
                                badge.setStyle(badge.getStyle() + "-fx-background-color: #CCFBF1; -fx-text-fill: #115E59;");
                                break;
                            case Diamond:
                                badge.setText("Kim Cương");
                                badge.setStyle(badge.getStyle() + "-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF;");
                                break;
                        }

                        setGraphic(badge);
                        setText(null);
                    }
                }
            };
        });
    }

    @FXML
    void Search(ActionEvent event) {
        // Logic tìm kiếm sẽ viết ở đây
    }

    @FXML
    void btnSortClick(ActionEvent event) {
        // Logic sắp xếp sẽ viết ở đây
    }
}