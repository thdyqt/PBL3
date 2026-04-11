package GUI;

import BusinessBLL.StaffBusiness;
import EntityDTO.Staff;
import Util.Others;
import Util.UserSession;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class StaffDialogController implements Initializable {
    @FXML
    private Label lblTitle;

    @FXML
    private Label lblDesc;

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

    @FXML
    void btnCancelClick(ActionEvent event) {
        closeForm();
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

        if (currentStaff != null) {
            boolean isNameUnchanged = name.equals(currentStaff.getName());
            boolean isPhoneUnchanged = phone.equals(currentStaff.getPhone());
            boolean isUserUnchanged = user.equals(currentStaff.getUser());
            boolean isRoleUnchanged = role != null && role.equals(currentStaff.getRole());
            boolean isPassUnchanged = pass.isEmpty();

            if (isEditingSelf) {
                isPassUnchanged = isPassUnchanged || pass.equals(UserSession.getInstance().getPassword());
            }

            boolean isDateUnchanged = currentStaff.getHire_date() != null && hire_date.toString().equals(currentStaff.getHire_date().toString());

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

        if (isEditingSelf) {
            if (!confirmCurrentPassword(pass)) return;
        }

        btnCancel.setDisable(true);
        btnSave.setDisable(true);
        Others.showAlert(mainPanel, "Đang kết nối máy chủ...", false);


        new Thread(() -> {
            int status = (currentStaff == null) ? StaffBusiness.register(phone, name, user, pass, role, hire_date)
                    : StaffBusiness.updateStaff(currentStaff.getId(), phone, name, user, pass, role, hire_date);

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

    private boolean confirmCurrentPassword(String newPassword) {
        final boolean[] isConfirmed = {false};

        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(txtName.getScene().getWindow());

        VBox root = new VBox(12);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #E2E8F0; -fx-border-width: 1;");

        DropShadow shadow = new DropShadow(20, new Color(0, 0, 0, 0.15));
        root.setEffect(shadow);

        Label lblHeader = new Label("Xác nhận bảo mật");
        lblHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        Label lblDesc = new Label("Vui lòng xác nhận mật khẩu để lưu các thay đổi:");
        lblDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        PasswordField pwdCurrentField = new PasswordField();
        pwdCurrentField.setPromptText("Nhập mật khẩu hiện tại...");
        pwdCurrentField.setStyle("-fx-font-size: 14px; -fx-padding: 8; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #CBD5E1; -fx-background-color: #F8FAFC;");
        pwdCurrentField.setPrefWidth(320);

        PasswordField pwdConfirmNewField = new PasswordField();
        pwdConfirmNewField.setPromptText("Xác nhận lại mật khẩu mới...");
        pwdConfirmNewField.setStyle("-fx-font-size: 14px; -fx-padding: 8; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #CBD5E1; -fx-background-color: #F8FAFC;");
        pwdConfirmNewField.setPrefWidth(320);

        boolean isChangingPassword = newPassword != null && !newPassword.isEmpty();
        if (!isChangingPassword) {
            pwdConfirmNewField.setVisible(false);
            pwdConfirmNewField.setManaged(false);
        }

        Label lblError = new Label("");
        lblError.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: bold;");
        lblError.setPrefHeight(15);
        lblError.setWrapText(true);

        Button btnCancelPopup = new Button("Hủy bỏ");
        btnCancelPopup.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        btnCancelPopup.setOnAction(e -> stage.close());

        Button btnConfirmPopup = new Button("Xác nhận");
        btnConfirmPopup.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");

        Runnable checkAction = () -> {
            boolean isCurrentCorrect = pwdCurrentField.getText().equals(UserSession.getInstance().getPassword());
            boolean isNewCorrect = !isChangingPassword || pwdConfirmNewField.getText().equals(newPassword);

            if (!isCurrentCorrect) {
                lblError.setText("Mật khẩu hiện tại không chính xác!");
                pwdCurrentField.clear();
                pwdCurrentField.requestFocus();
            } else if (!isNewCorrect) {
                lblError.setText("Mật khẩu xác nhận không trùng khớp với mật khẩu mới!");
                pwdConfirmNewField.clear();
                pwdConfirmNewField.requestFocus();
            } else {
                isConfirmed[0] = true;
                stage.close();
            }
        };

        btnConfirmPopup.setOnAction(e -> checkAction.run());
        pwdCurrentField.setOnAction(e -> {
            if (isChangingPassword) pwdConfirmNewField.requestFocus(); // Nhấn enter tự nhảy xuống ô dưới
            else checkAction.run();
        });
        pwdConfirmNewField.setOnAction(e -> checkAction.run()); // Ở ô dưới nhấn Enter là check luôn

        HBox buttonBox = new HBox(10, btnCancelPopup, btnConfirmPopup);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(lblHeader, lblDesc, pwdCurrentField, pwdConfirmNewField, lblError, buttonBox);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        Platform.runLater(pwdCurrentField::requestFocus); // Tự đưa con trỏ chuột vào ô pass hiện tại
        stage.showAndWait();

        return isConfirmed[0];
    }
}
