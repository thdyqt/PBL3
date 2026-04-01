package GUI;

import Business.StaffBusiness;
import Entity.Staff;
import Util.Others;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class StaffDialogController implements Initializable {
    @FXML
    private Label lblTitle;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private ComboBox<String> cbRole;

    @FXML
    private DatePicker dpHireDate;

    @FXML
    private AnchorPane mainPanel;

    @FXML
    private TextField txtName;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtUsername;

    private Staff currentStaff = null;
    private boolean saveSuccess;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dpHireDate.setValue(LocalDate.now());
        cbRole.getItems().addAll("Staff", "Admin");
        cbRole.getSelectionModel().selectFirst();

        Others.setMaxLength(txtPhone, 10);
        Others.setMaxLength(txtUsername, 20);
        Others.setMaxLength(txtName, 100);
        Others.setMaxLength(txtPassword, 20);

        txtPhone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtPhone.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        txtUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains(" ")) {
                txtUsername.setText(newValue.replaceAll(" ", ""));
            }
        });

        txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[\\p{L} .'-]*$")) {
                txtName.setText(newValue.replaceAll("[^\\p{L} .'-]", ""));
            }
        });

        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains(" ")) {
                txtPassword.setText(newValue.replaceAll(" ", ""));
            }
        });
    }

    public boolean isSaveSuccess() {
        return saveSuccess;
    }

    void closeForm(){
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }

    @FXML
    void btnCancelClick(ActionEvent event) {
        closeForm();
    }

    public void setStaffData(Staff staff) {
        this.currentStaff = staff;

        if (staff != null) {
            lblTitle.setText("CHỈNH SỬA NHÂN VIÊN");
            txtName.setText(staff.getName());
            txtPhone.setText(staff.getPhone());
            txtUsername.setText(staff.getUser());
            cbRole.setValue(staff.getRole());
            if (staff.getHire_date() != null) {
                dpHireDate.setValue(staff.getHire_date().toLocalDate());
            }
            txtPassword.setPromptText("Để trống nếu không đổi mật khẩu");
        }
    }

    @FXML
    void btnSaveClick(ActionEvent event) {
        String name = Others.standardizeName(txtName.getText());
        String phone = txtPhone.getText().trim();
        String user = txtUsername.getText().trim();
        String pass = txtPassword.getText().trim();
        String role = cbRole.getValue();
        Date hire_date = dpHireDate.getValue() != null ? Date.valueOf(dpHireDate.getValue()) : new Date(System.currentTimeMillis());

        if (name.isEmpty() || phone.isEmpty() || user.isEmpty() || (currentStaff == null && pass.isEmpty())){
            Others.showAlert(mainPanel, "Vui lòng điền đầy đủ thông tin bắt buộc!", true);
            return;
        }

        else if (!phone.matches("^0[0-9]{9}$")) {
            Others.showAlert(mainPanel, "Số điện thoại không hợp lệ!", true);
            txtPhone.requestFocus();
            return;
        }

        else if (user.length() < 6) {
            Others.showAlert(mainPanel, "Tài khoản phải từ 6 kí tự!", true);
            txtUsername.requestFocus();
            return;
        }

        else if ((currentStaff == null && pass.length() < 6) || (currentStaff != null && pass.length() > 0 && pass.length() < 6)) {
            Others.showAlert(mainPanel, "Mật khẩu phải từ 6 kí tự trở lên!", true);
            txtPassword.requestFocus();
            return;
        }

        else {
            btnCancel.setDisable(true);
            btnSave.setDisable(true);
            Others.showAlert(mainPanel, "Đang kết nối máy chủ...", false);
        }

        new Thread(() -> {
            int status = (currentStaff == null) ? StaffBusiness.register(phone, name, user, pass, role, hire_date)
                : StaffBusiness.updateStaff(currentStaff.getId(), phone, name, user, pass, role, hire_date);

            Platform.runLater(() -> {
                btnCancel.setDisable(false);
                btnSave.setDisable(false);

                if (status == 1){
                    if (currentStaff == null) Others.showAlert(mainPanel,"Đăng ký tài khoản thành công!", false);
                    else Others.showAlert(mainPanel,"Chỉnh sửa tài khoản thành công!", false);
                    saveSuccess = true;
                    var delay = new PauseTransition(Duration.seconds(2.5));
                    delay.setOnFinished(e -> closeForm());
                    delay.play();
                }
                else if (status == -1){
                    Others.showAlert(mainPanel, "Tên đăng nhập hoặc Số điện thoại đã được sử dụng!", true);
                }
                else {
                    Others.showAlert(mainPanel, "Lỗi kết nối máy chủ dữ liệu!", true);
                }
            });
        }).start();
    }
}
