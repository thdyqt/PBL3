package GUI;

import DataDAL.CustomerData;
import EntityDTO.Customer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;

public class CustomerManagementForm {

    @FXML
    private Button btnSort;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnEdit;

    @FXML
    private ComboBox<String> cbbSort;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<Customer> tableCustomer;

    // Khai báo các cột
    @FXML
    private TableColumn<Customer, Void> colSTT; // Cột STT mới thêm

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


    private ObservableList<Customer> customerList;
    private FilteredList<Customer> filteredData;

    @FXML
    public void initialize() {
        // 1. Ánh xạ dữ liệu cho các cột
        colId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        colPoint.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPoint()));

        // TỰ ĐỘNG ĐÁNH SỐ THỨ TỰ CHO CỘT STT
        colSTT.setCellFactory(column -> new TableCell<Customer, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        // 2. Cấu hình ComboBox sắp xếp
        ObservableList<String> sortOptions = FXCollections.observableArrayList(
                "Tên: A - Z",
                "Tên: Z - A",
                "Điểm: Cao - Thấp",
                "Điểm: Thấp - Cao",
                "Mã Khách: Mới nhất"
        );
        cbbSort.setItems(sortOptions);
        cbbSort.getSelectionModel().selectFirst();

        // 3. Cấu hình cột hiển thị Hạng
        setupRankColumn();

        // 4. Tải dữ liệu
        loadData();
    }

    private void loadData() {
        List<Customer> listCustomers = CustomerData.getAllCustomers();
        customerList = FXCollections.observableArrayList(listCustomers);

        filteredData = new FilteredList<>(customerList, b -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                if (customer.getName().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (customer.getPhone().contains(lowerCaseFilter)) return true;
                else if (String.valueOf(customer.getId()).contains(lowerCaseFilter)) return true;

                return false;
            });
        });

        tableCustomer.setItems(filteredData);
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
    void Search(ActionEvent event) { }

    @FXML
    void btnAddClick(ActionEvent event) {
        openDialog(null);
    }

    @FXML
    void btnEditClick(ActionEvent event) {
        Customer selected = tableCustomer.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một khách hàng trong bảng để sửa!").showAndWait();
            return;
        }
        openDialog(selected);
    }

    // Hàm mở Dialog dùng chung cho cả Thêm và Sửa
    private void openDialog(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/CustomerDialog.fxml"));
            Parent root = loader.load();

            CustomerDialogController controller = loader.getController();

            // Nếu có đối tượng truyền vào tức là đang Sửa
            if (customer != null) {
                controller.setCustomerData(customer);
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(customer == null ? "Thêm khách hàng" : "Sửa khách hàng");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            // Load lại bảng
            loadData();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi mở form!");
        }
    }

    @FXML
    void btnSortClick(ActionEvent event) {
        if (customerList == null || customerList.isEmpty()) return;

        String selectedOption = cbbSort.getValue();
        if (selectedOption == null) return;

        Comparator<Customer> comparator = null;

        switch (selectedOption) {
            case "Tên: A - Z":
                comparator = Comparator.comparing(Customer::getName);
                break;
            case "Tên: Z - A":
                comparator = Comparator.comparing(Customer::getName).reversed();
                break;
            case "Điểm: Cao - Thấp":
                comparator = Comparator.comparingInt(Customer::getPoint).reversed();
                break;
            case "Điểm: Thấp - Cao":
                comparator = Comparator.comparingInt(Customer::getPoint);
                break;
            case "Mã Khách: Mới nhất":
                comparator = Comparator.comparingInt(Customer::getId).reversed();
                break;
        }

        if (comparator != null) {
            FXCollections.sort(customerList, comparator);
        }
    }
}