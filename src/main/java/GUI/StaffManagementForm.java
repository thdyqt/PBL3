package GUI;

import Data.StaffData;
import Entity.Staff;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;

public class StaffManagementForm implements Initializable {
    @FXML
    private Button btnAdd;

    @FXML
    private Button btnEdit;

    @FXML
    private TableColumn<Staff, Date> colDate;

    @FXML
    private TableColumn<Staff, Integer> colId;

    @FXML
    private TableColumn<Staff, String> colName;

    @FXML
    private TableColumn<Staff, String> colPhone;

    @FXML
    private TableColumn<Staff, String> colRole;

    @FXML
    private TableColumn<Staff, String> colUsername;

    @FXML
    private TableView<Staff> tblStaff;

    @FXML
    private TextField txtSearch;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTable();
    }

    public void loadTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("user"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("hire_date"));

        List<Staff> listFromDB = StaffData.getAllStaff();
        ObservableList<Staff> staffList = FXCollections.observableArrayList(listFromDB);
        tblStaff.setItems(staffList);
    }

    @FXML
    void btnAddClick(ActionEvent event) {

    }

    @FXML
    void btnEditClick(ActionEvent event) {

    }
}
