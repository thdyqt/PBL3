package GUI;

import DataDAL.StaffData;
import EntityDTO.Staff;
import Util.Others;
import Util.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class StaffManagementForm implements Initializable {
    @FXML
    private BorderPane mainPane;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnEdit;

    @FXML
    private TableColumn<Staff, Date> colDate;

    @FXML
    private TableColumn<Staff, Void> colSTT;

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

    private boolean saveSuccess = false;
    private ObservableList<Staff> masterData = FXCollections.observableArrayList();
    private FilteredList<Staff> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableStyles();
        loadTable();
        setupSearch();
    }

    private void setupTableStyles() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("user"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("hire_date"));

        colSTT.setCellFactory(column -> new TableCell<Staff, Void>() {
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
        colUsername.setStyle("-fx-alignment: CENTER;");
        colName.setStyle("-fx-alignment: CENTER_LEFT; -fx-font-weight: bold; -fx-text-fill: #0F172A; -fx-padding: 0 0 0 15;");

        colRole.setCellFactory(column -> new TableCell<Staff, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label lbl = new Label(item);
                    String style = "-fx-background-color: #E0E7FF; -fx-text-fill: #3730A3; -fx-padding: 4 10 4 10; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 11px;";

                    if (item.toLowerCase().contains("quản lý") || item.toLowerCase().contains("admin")) {
                        style = "-fx-background-color: #FEF08A; -fx-text-fill: #854D0E; -fx-padding: 4 10 4 10; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 11px;";
                    }
                    lbl.setStyle(style);
                    setGraphic(lbl);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        colDate.setCellFactory(column -> new TableCell<Staff, Date>() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(sdf.format(item));
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });
    }

    private void loadTable() {
        List<Staff> listFromDB = StaffData.getAllStaff();
        masterData.setAll(listFromDB);
        filteredData = new FilteredList<>(masterData, b -> true);
        SortedList<Staff> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblStaff.comparatorProperty());
        tblStaff.setItems(sortedData);
        Others.animateTableRows(tblStaff);
    }

    private void setupSearch() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(staff -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();

                String hireDateStr = "";
                if (staff.getHire_date() != null) {
                    hireDateStr = sdf.format(staff.getHire_date());
                }

                if (staff.getName().toLowerCase().contains(searchKeyword) ||
                        staff.getPhone().toLowerCase().contains(searchKeyword) ||
                        staff.getUser().toLowerCase().contains(searchKeyword) ||
                        (staff.getRole() != null && staff.getRole().toLowerCase().contains(searchKeyword)) ||
                        hireDateStr.contains(searchKeyword)) {
                    return true;
                }

                return false;
            });
        });
    }

    private void openStaffDialog(Staff staffToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("staffDialog.fxml"));
            Parent root = loader.load();

            StaffDialogController controller = loader.getController();

            controller.setStaffData(staffToEdit);

            Stage stage = new Stage();
            stage.setTitle(staffToEdit == null ? "Thêm nhân viên mới" : "Chỉnh sửa nhân viên");
            stage.setScene(new Scene(root));

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            if (controller.isSaveSuccess()) {
                loadTable();
                Others.animateTableRows(tblStaff);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnAddClick(ActionEvent event) {
        openStaffDialog(null);
    }

    @FXML
    private void btnEditClick(ActionEvent event) {
        Staff selectedStaff = tblStaff.getSelectionModel().getSelectedItem();
        if (selectedStaff == null){
            Others.showAlert(mainPane, "Vui lòng chọn nhân viên!", true);
            return;
        }
        openStaffDialog(selectedStaff);
    }

    @FXML
    private void btnResignClick(ActionEvent event) {
        Staff selectedStaff = tblStaff.getSelectionModel().getSelectedItem();

        if (selectedStaff == null){
            Others.showAlert(mainPane, "Vui lòng chọn nhân viên!", true);
            return;
        }

        if (selectedStaff.getId() == UserSession.getInstance().getId()){
            Others.showAlert(mainPane, "Bạn không thể thôi việc chính mình!", true);
            return;
        }

        boolean isConfirm = Others.showCustomConfirm(
                "Thôi việc nhân viên",
                "Bạn đang yêu cầu thôi việc nhân viên này khỏi hệ thống.\nBạn có chắc chắn muốn thực hiện không?",
                "Xác nhận", "Hủy bỏ"
        );

        if (isConfirm) {
            int result = BusinessBLL.StaffBusiness.resignStaff(selectedStaff.getId(), selectedStaff.getName(), selectedStaff.getUser());

            if (result == 1) {
                Util.Others.showAlert(mainPane,"Cho thôi việc nhân viên thành công!", false);
                loadTable();
                Util.Others.animateTableRows(tblStaff);
            } else {
                Util.Others.showAlert(mainPane, "Lỗi kết nối máy chủ dữ liệu!", true);
            }
        }
    }
}
