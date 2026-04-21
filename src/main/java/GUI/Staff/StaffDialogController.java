package GUI.Staff;

import BusinessBLL.StaffBusiness;
import EntityDTO.Staff;
import Util.Others;
import Util.UserSession;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class StaffDialogController implements Initializable {
    @FXML private Label lblTitle;
    @FXML private Label lblDesc;
    @FXML private Button btnCancel;
    @FXML private Button btnSave;
    @FXML private ComboBox<String> cbRole;
    @FXML private DatePicker dpHireDate;
    @FXML private AnchorPane mainPanel;
    @FXML private TextField txtName;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPhone;
    @FXML private TextField txtUsername;

    private Staff currentStaff = null;
    private boolean saveSuccess;
    private boolean isEditingSelf = false;

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

    void closeForm() {
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }

    public void setStaffData(Staff staff) {
        this.currentStaff = staff;
        if (staff == null) txtPassword.setText("123456");

        if (staff != null) {
            lblTitle.setText("CHỈNH SỬA THÔNG TIN NHÂN VIÊN");
            txtName.setText(staff.getName());
            txtPhone.setText(staff.getPhone());
            txtUsername.setText(staff.getUser());
            cbRole.setValue(staff.getRole());

            if (currentStaff.getId() == UserSession.getInstance().getId()) {
                cbRole.setDisable(true);
            }

            if (staff.getHireDate() != null) {
                dpHireDate.setValue(staff.getHireDate().toLocalDate());
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

        if (currentStaff != null) {
            boolean isNameUnchanged = name.equals(currentStaff.getName());
            boolean isPhoneUnchanged = phone.equals(currentStaff.getPhone());
            boolean isUserUnchanged = user.equals(currentStaff.getUser());
            boolean isRoleUnchanged = role != null && role.equals(currentStaff.getRole());
            boolean isPassUnchanged = pass.isEmpty();

            if (isEditingSelf) {
                isPassUnchanged = isPassUnchanged || pass.equals(UserSession.getInstance().getPassword());
            }

            boolean isDateUnchanged = currentStaff.getHireDate() != null && hire_date.toString().equals(currentStaff.getHireDate().toString());

            if (isNameUnchanged && isPhoneUnchanged && isUserUnchanged && isRoleUnchanged && isPassUnchanged && isDateUnchanged) {
                Others.showAlert(mainPanel, "Không có thông tin nào được thay đổi!", true);
                return;
            }
        }

        if (name.isEmpty() || phone.isEmpty() || user.isEmpty() || (currentStaff == null && pass.isEmpty())) {
            Others.showAlert(mainPanel, "Vui lòng điền đầy đủ thông tin bắt buộc!", true);
            return;
        }

        if (!phone.matches("^0[0-9]{9}$")) {
            Others.showAlert(mainPanel, "Số điện thoại không hợp lệ!", true);
            txtPhone.requestFocus();
            return;
        }

        if (user.length() < 6) {
            Others.showAlert(mainPanel, "Tài khoản phải từ 6 kí tự!", true);
            txtUsername.requestFocus();
            return;
        }

        if (currentStaff == null && pass.length() < 6) {
            Others.showAlert(mainPanel, "Mật khẩu phải từ 6 kí tự trở lên!", true);
            txtPassword.requestFocus();
            return;
        }

        if (currentStaff != null && !pass.isEmpty() && pass.length() < 6) {
            Others.showAlert(mainPanel, "Mật khẩu mới phải từ 6 kí tự trở lên!", true);
            txtPassword.requestFocus();
            return;
        }

        Window currentWindow = btnSave.getScene().getWindow();

        if (isEditingSelf) {
            if (!Others.showPasswordConfirmDialog(currentWindow, pass)) return;
        }

        btnCancel.setDisable(true);
        btnSave.setDisable(true);
        Others.showAlert(mainPanel, "Đang kết nối máy chủ...", false);

        new Thread(() -> {
            int status = (currentStaff == null) ? StaffBusiness.register(phone, name, user, pass, role, hire_date, "Active")
                    : StaffBusiness.updateStaff(currentStaff.getId(), phone, name, user, pass, role, hire_date, currentStaff.getStatus());

            Platform.runLater(() -> {
                btnCancel.setDisable(false);
                btnSave.setDisable(false);

                if (status == 1) {
                    if (currentStaff == null) Others.showAlert(mainPanel, "Đăng ký tài khoản thành công!", false);
                    else Others.showAlert(mainPanel, "Chỉnh sửa tài khoản thành công!", false);
                    saveSuccess = true;

                    if (isEditingSelf) {
                        String sessionPass = pass.isEmpty() ? UserSession.getInstance().getPassword() : pass;
                        UserSession.getInstance().setStaff(UserSession.getInstance().getId(), phone, name, user, sessionPass, role, hire_date);
                    }

                    var delay = new PauseTransition(Duration.seconds(2.5));
                    delay.setOnFinished(e -> closeForm());
                    delay.play();
                } else if (status == -1) {
                    Others.showAlert(mainPanel, "Tên đăng nhập hoặc Số điện thoại đã được sử dụng!", true);
                } else {
                    Others.showAlert(mainPanel, "Lỗi kết nối máy chủ dữ liệu!", true);
                }
            });
        }).start();
    }

    @FXML
    void btnCancelClick(ActionEvent event) {
        closeForm();
    }

    public void setViewOnlyMode() {
        lblTitle.setText("THÔNG TIN TÀI KHOẢN");
        lblDesc.setText("");

        txtName.setEditable(false);
        txtPhone.setEditable(false);
        txtUsername.setEditable(false);
        txtPassword.setEditable(false);
        cbRole.setDisable(true);
        dpHireDate.setDisable(true);
        txtPassword.setPromptText("Đã bảo mật");

        btnSave.setVisible(false);
        btnCancel.setText("Đóng");
    }

    public void setProfileEditMode() {
        this.isEditingSelf = true;

        lblTitle.setText("CHỈNH SỬA THÔNG TIN CÁ NHÂN");
        txtUsername.setEditable(false);
        txtUsername.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #64748B;");
        dpHireDate.setDisable(true);
        cbRole.setDisable(true);
    }
}