package GUI;

import Business.CustomerBusiness;
import Business.StaffBusiness;
import Util.Others;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginForm implements Initializable {
    @FXML
    private StackPane rootPane;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnRegister;

    @FXML
    private HBox mainForm;

    @FXML
    private RadioButton rbCustomer;

    @FXML
    private RadioButton rbStaff;

    @FXML
    private ToggleGroup roleGroup;

    @FXML
    private PasswordField txtPass;

    @FXML
    private TextField txtUser;

    private int failedAttempts = 0;

    @FXML
    void btnLoginClick(ActionEvent event) {
        String user = txtUser.getText().trim();
        String pass = txtPass.getText().trim();

        if (user.isEmpty() && pass.isEmpty()) {
            Others.showAlert(rootPane, "Vui lòng điền đầy đủ tài khoản và mật khẩu!", true);
            txtUser.requestFocus();
            return;
        }

        else if (user.isEmpty()){
            Others.showAlert(rootPane, "Vui lòng điền tài khoản!", true);
            txtUser.requestFocus();
            return;
        }

        else if (pass.isEmpty()){
            Others.showAlert(rootPane, "Vui lòng điền mật khẩu!", true);
            txtPass.requestFocus();
            return;
        }

        else if (user.length() < 6 || pass.length() < 6) {
            Others.showAlert(rootPane, "Tài khoản hoặc mật khẩu phải từ 6 kí tự!", true);
            txtUser.requestFocus();
            return;
        }

        else {
            btnLogin.setDisable(true);
            btnRegister.setDisable(true);
            Others.showAlert(rootPane, "Đang kết nối máy chủ...", false);

            new Thread(() -> {
                int loginStatus = (rbCustomer.isSelected()) ? CustomerBusiness.login(user, pass) : StaffBusiness.login(user, pass);

                javafx.application.Platform.runLater(() -> {
                    btnLogin.setDisable(false);
                    btnRegister.setDisable(false);

                    if (loginStatus == 1){
                        failedAttempts = 0;
                        Others.showAlert(rootPane,"Đăng nhập thành công!", false);
                        txtPass.clear();

                        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2.5));
                        delay.setOnFinished(actionEvent -> {
                            try {
                                FXMLLoader loader = null;
                                if (rbStaff.isSelected()) loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                                Parent root = loader.load();

                                btnLogin.getScene().setRoot(root);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Others.showAlert(rootPane, "Không thể chuyển sang giao diện chính!", true);
                            }
                        });
                        delay.play();
                    }
                    else if (loginStatus == 2){
                        handleFailedLogin("Mật khẩu không chính xác!");
                        txtPass.clear();
                        txtPass.requestFocus();
                    }
                    else if (loginStatus == 0){
                        handleFailedLogin("Tài khoản không tồn tại!");
                        txtUser.clear();
                        txtUser.requestFocus();
                    }
                    else {
                        Others.showAlert(rootPane, "Lỗi kết nối máy chủ dữ liệu!", true);
                    }
                });
            }).start();
        }
    }

    @FXML
    void btnRegisterClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
            Parent root = loader.load();

            btnRegister.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
            Others.showAlert(rootPane, "Không thể mở màn hình đăng ký!", true);
        }
    }

    //EFFECTS
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Others.playFormAnimation(mainForm);

        Util.Others.setMaxLength(txtUser, 20);
        Util.Others.setMaxLength(txtPass, 20);

        txtUser.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains(" ")) {
                txtUser.setText(newValue.replaceAll(" ", ""));
            }
        });

        txtPass.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains(" ")) {
                txtPass.setText(newValue.replaceAll(" ", ""));
            }
        });
    }

    private void handleFailedLogin(String message) {
        failedAttempts++;
        if (failedAttempts >= 5) {
            btnLogin.setDisable(true);
            btnRegister.setDisable(true);
            Others.showAlert(rootPane, "Khóa tạm thời 60 giây do nhập sai quá 5 lần!", true);

            javafx.animation.PauseTransition lockTimer = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(60));
            lockTimer.setOnFinished(e -> {
                failedAttempts = 0;
                btnLogin.setDisable(false);
                btnRegister.setDisable(false);
                Others.showAlert(rootPane, "Đã mở khóa. Vui lòng thử lại!", false);
            });
            lockTimer.play();

        } else {
            Others.showAlert(rootPane, message + " (Sai " + failedAttempts + "/5 lần)", true);
        }
    }
}
