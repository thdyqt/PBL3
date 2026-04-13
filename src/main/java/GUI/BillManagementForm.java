package GUI;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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

    private ObservableList<EntityDTO.Order> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<String> searchOptions = FXCollections.observableArrayList(
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
    }

    private void setupTable(){
        col_OrderID.setCellValueFactory(new PropertyValueFactory<>("id"));
        col_OrderStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

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

        colProcessStaffName.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStaff() != null) {
                return new SimpleStringProperty(cellData.getValue().getStaff().getName());
            }
            return new SimpleStringProperty("Không rõ"); // Unknown
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
            return new SimpleObjectProperty<>(null); // Leave blank if it's a Walk-in
        });

        col_CustomerName.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCustomer() != null) {
                return new SimpleStringProperty(cellData.getValue().getCustomer().getName());
            }
            return new SimpleStringProperty("Khách vãng lai"); // Walk-in Customer
        });

        col_PhoneCustomer.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCustomer() != null) {
                return new SimpleStringProperty(cellData.getValue().getCustomer().getPhone());
            }
            return new SimpleStringProperty(""); // Blank for walk-ins
        });
    }

    private void loadTable(){
        java.util.List<EntityDTO.Order> list = BusinessBLL.OrderBusiness.getAllOrder_BLL();

        masterData.setAll(list);

        tbOrder.setItems(masterData);
    }

}
