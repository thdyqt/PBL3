package GUI;

import DataDAL.CustomerData;
import EntityDTO.Customer;
import Util.Others;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerManagementForm implements Initializable {

    @FXML
    private StackPane rootPane;

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

    @FXML
    private TableColumn<Customer, Void> colSTT;

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

    private ObservableList<Customer> masterData = FXCollections.observableArrayList();
    private FilteredList<Customer> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableStyles();
        setupRankColumn();

        // Cấu hình ComboBox sắp xếp
        ObservableList<String> sortOptions = FXCollections.observableArrayList(
                "Tên: A - Z", "Tên: Z - A", "Điểm: Cao - Thấp", "Điểm: Thấp - Cao", "Mã Khách: Mới nhất"
        );
        cbbSort.setItems(sortOptions);
        cbbSort.getSelectionModel().selectFirst();

        loadTable();
        setupSearch();
    }

    private void setupTableStyles() {
        colId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        colPoint.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPoint()));

        // TỰ ĐỘNG ĐÁNH SỐ THỨ TỰ
        colSTT.setCellFactory(column -> new TableCell<Customer, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                    setStyle("-fx-alignment: CENTER; -fx-text-fill: #64748B;");
                }
            }
        });

        colId.setStyle("-fx-alignment: CENTER; -fx-text-fill: #64748B;");
        colPhone.setStyle("-fx-alignment: CENTER;");
        colPoint.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-text-fill: #EF4444;");
        colName.setStyle("-fx-alignment: CENTER_LEFT; -fx-font-weight: bold; -fx-text-fill: #0F172A; -fx-padding: 0 0 0 15;");
    }

    private void setupRankColumn() {
        colRank.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCustomer_rank())
        );

        colRank.setCellFactory(column -> new TableCell<Customer, Customer.rank>() {
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
                        case Emerald:
                            badge.setText("Ngọc Lục Bảo");
                            badge.setStyle(badge.getStyle() + "-fx-background-color: #CCFBF1; -fx-text-fill: #115E59;");
                            break;
                        case Diamond:
                            badge.setText("Kim Cương");
                            badge.setStyle(badge.getStyle() + "-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF;");
                            break;
                    }
                    setGraphic(badge);
                    setText(null);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });
    }

    private void loadTable() {
        new Thread(() -> {
            List<Customer> listFromDB = CustomerData.getAllCustomers();
            Platform.runLater(() -> {
                masterData.setAll(listFromDB);
                filteredData = new FilteredList<>(masterData, b -> true);
                SortedList<Customer> sortedData = new SortedList<>(filteredData);
                sortedData.comparatorProperty().bind(tableCustomer.comparatorProperty());
                tableCustomer.setItems(sortedData);
                Others.animateTableRows(tableCustomer);
            });
        }).start();
}

    private void setupSearch() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                if (newValue == null || newValue.trim().isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                return customer.getName().toLowerCase().contains(lowerCaseFilter) ||
                        customer.getPhone().contains(lowerCaseFilter) ||
                        customer.getUser().toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(customer.getId()).contains(lowerCaseFilter);
            });
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
            // Hiển thị thông báo đẹp nếu lấy rootPane (hoặc MainBorderPane bên Dashboard)
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một khách hàng trong bảng để sửa!").showAndWait();
            return;
        }
        openDialog(selected);
    }

    private void openDialog(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/CustomerDialog.fxml"));
            Parent root = loader.load();

            CustomerDialogController controller = loader.getController();
            controller.setCustomerData(customer);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(customer == null ? "Thêm khách hàng" : "Sửa khách hàng");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            // Nếu lưu DB thành công thì mới load lại bảng
            if(controller.isSaveSuccess()) {
                loadTable();
                Others.animateTableRows(tableCustomer);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi mở form Customer Dialog!");
        }
    }

    @FXML
    void btnSortClick(ActionEvent event) {
        if (masterData == null || masterData.isEmpty()) return;

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
            FXCollections.sort(masterData, comparator);
        }
    }
}