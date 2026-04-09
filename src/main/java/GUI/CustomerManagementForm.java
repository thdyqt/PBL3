package GUI;

import DataDAL.CustomerData;
import EntityDTO.Customer;
import Util.Others;
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
    private TableColumn<Customer, String> colName;

    @FXML
    private TableColumn<Customer, String> colPhone;

    @FXML
    private TableColumn<Customer, String> colUser;

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

        ObservableList<String> sortOptions = FXCollections.observableArrayList(
                "Tên: A - Z", "Tên: Z - A", "Điểm: Cao - Thấp", "Điểm: Thấp - Cao", "Mặc định"
        );
        cbbSort.setItems(sortOptions);
        cbbSort.getSelectionModel().select("Mặc định");

        loadTable();
        setupSearch();

        tableCustomer.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Customer selectedCustomer = tableCustomer.getSelectionModel().getSelectedItem();
                if (selectedCustomer != null) {
                    openDialog(selectedCustomer);
                }
            }
        });
    }

    private void setupTableStyles() {
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        colPoint.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPoint()));
        colUser.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser()));

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

        colPhone.setStyle("-fx-alignment: CENTER;");
        colUser.setStyle("-fx-alignment: CENTER;");
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
        List<Customer> listFromDB = CustomerData.getAllCustomers();
        masterData.setAll(listFromDB);
        filteredData = new FilteredList<>(masterData, b -> true);
        SortedList<Customer> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableCustomer.comparatorProperty());
        tableCustomer.setItems(sortedData);
        Others.animateTableRows(tableCustomer);
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                if (newValue == null || newValue.trim().isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();

                String rankName = "";
                if (customer.getCustomer_rank() != null) {
                    switch (customer.getCustomer_rank()) {
                        case Bronze: rankName = "đồng"; break;
                        case Silver: rankName = "bạc"; break;
                        case Gold: rankName = "vàng"; break;
                        case Emerald: rankName = "ngọc lục bảo"; break;
                        case Diamond: rankName = "kim cương"; break;
                    }
                }

                return customer.getName().toLowerCase().contains(lowerCaseFilter) ||
                        customer.getPhone().contains(lowerCaseFilter) ||
                        (customer.getUser() != null && customer.getUser().toLowerCase().contains(lowerCaseFilter)) ||
                        String.valueOf(customer.getId()).contains(lowerCaseFilter) ||
                        rankName.contains(lowerCaseFilter);
            });
        });
    }

    @FXML
    void Search(ActionEvent event) {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(staff -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();

                if (staff.getName().toLowerCase().contains(searchKeyword) ||
                        staff.getPhone().toLowerCase().contains(searchKeyword) ||
                        staff.getUser().toLowerCase().contains(searchKeyword) ||
                        String.valueOf(staff.getPoint()).contains(searchKeyword)){
                    return true;
                }

                return false;
            });
        });
    }

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
                comparator = (c1, c2) -> {
                    String ten1 = Others.getFirstName(c1.getName()).toLowerCase();
                    String ten2 = Others.getFirstName(c2.getName()).toLowerCase();
                    int cmp = ten1.compareTo(ten2);

                    if (cmp == 0) {
                        return c1.getName().toLowerCase().compareTo(c2.getName().toLowerCase());
                    }
                    return cmp;
                };
                break;
            case "Tên: Z - A":
                comparator = (c1, c2) -> {
                    String ten1 = Others.getFirstName(c1.getName()).toLowerCase();
                    String ten2 = Others.getFirstName(c2.getName()).toLowerCase();
                    int cmp = ten2.compareTo(ten1);

                    if (cmp == 0) {
                        return c2.getName().toLowerCase().compareTo(c1.getName().toLowerCase());
                    }
                    return cmp;
                };
                break;
            case "Điểm: Cao - Thấp":
                comparator = Comparator.comparingInt(Customer::getPoint).reversed();
                break;
            case "Điểm: Thấp - Cao":
                comparator = Comparator.comparingInt(Customer::getPoint);
                break;
            case "Mặc định":
                comparator = Comparator.comparingInt(Customer::getId);
                break;
        }

        if (comparator != null) {
            tableCustomer.getSortOrder().clear();
            FXCollections.sort(masterData, comparator);
        }
    }
}