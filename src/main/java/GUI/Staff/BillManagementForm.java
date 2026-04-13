package GUI.Staff;

import Util.Others;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.collections.ObservableList;
import org.w3c.dom.Entity;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class BillManagementForm implements Initializable{

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
    private TableColumn<EntityDTO.Order, Integer> col_CustomerID;

    @FXML
    private TableColumn<EntityDTO.Order, String> col_CustomerName;

    @FXML
    private TableColumn<EntityDTO.Order, Integer> col_OrderID;

    @FXML
    private TableColumn<EntityDTO.Order, String> col_OrderStatus;

    @FXML
    private TableColumn<EntityDTO.Order, String> col_PhoneCustomer;

    @FXML
    private TableColumn<EntityDTO.Order, Integer> col_StaffID;

    @FXML
    private BorderPane mainPane;

    @FXML
    private TableView<EntityDTO.Order> tbOrder;

    @FXML
    private TextField txtSearch;

    //objects
    //an active version of the database
    //will update itself when changes are made
    private ObservableList<EntityDTO.Order> masterData = FXCollections.observableArrayList();
    //for search function
    //work by hiding unrelated data
    private FilteredList<EntityDTO.Order> filteredData;


    //methods
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<String> searchOptions = FXCollections.observableArrayList(
                "Chọn tiêu chí tìm kiếm",
                "Tìm kiếm theo ID order",
                "Tìm kiếm theo ID nhân viên thực hiện",
                "Tìm kiếm theo SĐT khách hàng"
        );

        //adding options to cbb
        cbbSearchOption.setItems(searchOptions);
        cbbSearchOption.getSelectionModel().selectFirst();

        //change prompt text of searcbox whenever a new cbb option is selected
        cbbSearchOption.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtSearch.setPromptText(newValue);
                txtSearch.clear();

                switch (newValue) {
                    case "Chọn tiêu chí tìm kiếm":
                        txtSearch.setPromptText("Chưa chọn tiêu chí tìm kiếm");
                        break;
                    case "Tìm kiếm theo ID order":
                        txtSearch.setPromptText("Nhập ID Đơn hàng");
                        break;
                    case "Tìm kiếm theo ID nhân viên thực hiện":
                        txtSearch.setPromptText("Nhập ID Nhân viên");
                        break;
                    case "Tìm kiếm theo SĐT khách hàng":
                        txtSearch.setPromptText("Nhập SĐT Khách hàng");
                        break;
                    default:
                        txtSearch.setPromptText("Nhập từ khóa tìm kiếm");
                }
            }
        });

        setupTable();
        loadTable();
        search();
    }

    private void setupTable(){
        //automatic search
        //work via calling getId() and getStatus
        //underlying mechanism
        //convert id into Id() -> add get into it to make getId()
        //then it looks for method named getId() in EntityDTO.Order
        //finally return the value of it
        //impractical
        //only use for those 2 because the naming of the getters in Order concidently
        col_OrderID.setCellValueFactory(new PropertyValueFactory<>("id"));
        col_OrderStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        //same thing as all the methods below
        //albeit modified to format the date
        colProcessTime.setCellValueFactory(new PropertyValueFactory<>("process_time"));
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

        //just get the value of Y in X if X is not null, yadda yadda
        colProcessStaffName.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStaff() != null) {
                return new SimpleStringProperty(cellData.getValue().getStaff().getName());
            }
            return new SimpleStringProperty("Name unspecified.");
        });

        col_StaffID.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStaff() != null) {
                return new SimpleObjectProperty<>(cellData.getValue().getStaff().getId());
            }
            return new SimpleObjectProperty<>(null);
        });

        col_CustomerID.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCustomer() != null && cellData.getValue().getCustomer().getId() > 0) {
                return new SimpleObjectProperty<>(cellData.getValue().getCustomer().getId());
            }
            return new SimpleObjectProperty<>(null);
        });

        col_CustomerName.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCustomer() != null) {
                return new SimpleStringProperty(cellData.getValue().getCustomer().getName());
            }
            return new SimpleStringProperty("Non registered customer");
        });

        col_PhoneCustomer.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCustomer() != null) {
                return new SimpleStringProperty(cellData.getValue().getCustomer().getPhone());
            }
            return new SimpleStringProperty("");
        });

        col_CustomerID.setStyle("-fx-alignment: CENTER;");
        col_OrderID.setStyle("-fx-alignment: CENTER;");
        col_CustomerName.setStyle("-fx-alignment: CENTER_LEFT; -fx-font-weight: bold; -fx-text-fill: #0F172A; -fx-padding: 0 0 0 15;");
        col_OrderStatus.setStyle("-fx-alignment: CENTER;");
        col_PhoneCustomer.setStyle("-fx-alignment: CENTER;");
        colProcessStaffName.setStyle("-fx-alignment: CENTER_LEFT; -fx-font-weight: bold; -fx-text-fill: #0F172A; -fx-padding: 0 0 0 15;");
        colProcessTime.setStyle("-fx-alignment: CENTER;");
        col_StaffID.setStyle("-fx-alignment: CENTER;");
    }

    private void search(){
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(order -> {
                //empty query
                if (newValue == null || newValue.trim().isEmpty()){
                    return true;
                }

                //normalizer
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
                    case "Tìm kiếm theo ID nhân viên thực hiện":
                        if (order.getStaff() != null){
                            return String.valueOf(order.getStaff().getId()).contains(keyword);
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
            //force reset when cbb option change
            cbbSearchOption.valueProperty().addListener((obs, oldVal, newVal) -> {
            String currentText = txtSearch.getText();
            txtSearch.setText("");
            txtSearch.setText(currentText);
        });
    }

    private void loadTable(){
        //load static database
        //with all the data unrestricted
        java.util.List<EntityDTO.Order> list = BusinessBLL.OrderBusiness.getAllOrder_BLL();
        //make it active
        masterData.setAll(list);

        //by default, filter nothing, let all go through
        filteredData = new FilteredList<>(masterData, temp -> true);

        SortedList<EntityDTO.Order> sortedData = new SortedList<>(filteredData);
        //sort binding
        sortedData.comparatorProperty().bind(tbOrder.comparatorProperty());

        tbOrder.setItems(sortedData);

        Others.animateTableRows(tbOrder);
    }

}
