package GUI;

import Business.CustomerBusiness;
import Util.Others;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterForm implements Initializable{
    @FXML
    private Button btnBackToLogin;

    @FXML
    private Button btnSubmitRegister;

    @FXML
    private HBox mainForm;

    @FXML
    private StackPane rootPane;

    @FXML
    private PasswordField txtConfirmPass;

    @FXML
    private TextField txtName;

    @FXML
    private PasswordField txtPass;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtUser;

    @FXML
    void btnBackToLoginClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            btnBackToLogin.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnSubmitRegisterClick(ActionEvent event) {
        String phone = txtPhone.getText().trim();
        String user = txtUser.getText().trim();
        String name = txtName.getText().trim();
        String pass = txtPass.getText().trim();
        String confirmPass = txtConfirmPass.getText().trim();

        if (phone.isEmpty() || name.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            Others.showAlert(rootPane, "Vui lòng điền đầy đủ thông tin bắt buộc!", true);
            return;
        }

        else if (!phone.matches("^[0-9]+$") || phone.length() != 10) {
            Others.showAlert(rootPane, "Số điện thoại phải bao gồm đúng 10 chữ số!", true);
            txtPhone.requestFocus();
            return;
        }

        else if (((user.length() < 6) || user.contains (" ")) && !user.isEmpty()) {
            Others.showAlert(rootPane, "Tài khoản phải từ 6 kí tự và không chứa khoảng trắng!", true);
            txtUser.requestFocus();
            return;
        }

        else if (!name.matches("^[\\p{L} .'-]+$")) {
            Others.showAlert(rootPane, "Họ tên không hợp lệ (không chứa số hoặc kí tự lạ)!", true);
            txtName.requestFocus();
            return;
        }

        else if (pass.length() < 6 || pass.contains (" ")) {
            Others.showAlert(rootPane, "Mật khẩu phải từ 6 kí tự và không chứa khoảng trắng!", true);
            txtPass.requestFocus();
            return;
        }

        else if (!pass.equals(confirmPass)) {
            Others.showAlert(rootPane, "Mật khẩu xác nhận không trùng khớp!", true);
            txtConfirmPass.requestFocus();
            return;
        }

        else {
            btnSubmitRegister.setDisable(true);
            btnBackToLogin.setDisable(true);
            Others.showAlert(rootPane, "Đang kết nối máy chủ...", false);

            new Thread(() -> {
                int registerStatus = CustomerBusiness.register(phone, name, user, pass);

                javafx.application.Platform.runLater(() -> {
                    btnSubmitRegister.setDisable(false);
                    btnBackToLogin.setDisable(false);

                    if (registerStatus == 1){
                        Others.showAlert(rootPane,"Đăng ký tài khoản thành công! Vui lòng đăng nhập.", false);
                        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2.5));
                        delay.setOnFinished(e -> btnBackToLoginClick(null));
                        delay.play();
                    }
                    else if (registerStatus == -1){
                        Others.showAlert(rootPane, "Tên đăng nhập hoặc Số điện thoại đã được sử dụng!", true);
                        return;
                    }
                    else {
                        Others.showAlert(rootPane, "Lỗi kết nối máy chủ dữ liệu!", true);
                    }
                });
            }).start();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Others.playFormAnimation(mainForm);
    }
}
