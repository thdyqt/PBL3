package GUI;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.animation.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.layout.*;
import javafx.util.*;
import javafx.geometry.*;

import java.net.*;
import java.util.*;

import Business.StaffBusiness;
import Business.CustomerBusiness;

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

    @FXML
    void btnLoginClick(ActionEvent event) {
        String user = txtUser.getText();
        String pass = txtPass.getText();

        if (user.isEmpty() && pass.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ tài khoản và mật khẩu!", true);
            txtUser.requestFocus();
            return;
        }

        else if (user.isEmpty()){
            showAlert("Vui lòng nhập tài khoản!", true);
            txtUser.requestFocus();
            return;
        }

        else if (pass.isEmpty()){
            showAlert("Vui lòng nhập mật khẩu!", true);
            txtPass.requestFocus();
        }

        else {
            if (rbStaff.isSelected()){
                btnLogin.setDisable(true);
                btnRegister.setDisable(true);
                showAlert("Đang kết nối máy chủ...", false);

                new Thread(() -> {
                    int loginStatus = StaffBusiness.login(user, pass);

                    javafx.application.Platform.runLater(() -> {
                        btnLogin.setDisable(false);
                        btnRegister.setDisable(false);

                        if (loginStatus == 1){
                            showAlert("Đăng nhập thành công!", false);
                        }
                        else if (loginStatus == 2){
                            showAlert("Mật khẩu không chính xác!", true);
                            txtPass.clear();
                            txtPass.requestFocus();
                        }
                        else if (loginStatus == 0){
                            showAlert("Tài khoản này không tồn tại!", true);
                            txtUser.clear();
                            txtUser.requestFocus();
                        }
                        else {
                            showAlert("Lỗi kết nối máy chủ dữ liệu!", true);
                        }
                    });
                }).start();
            }
            else if(rbCustomer.isSelected()){
                btnLogin.setDisable(true);
                btnRegister.setDisable(true);
                showAlert("Đang kết nối máy chủ...", false);

                new Thread(() -> {
                    int loginStatus = CustomerBusiness.login(user, pass);

                    javafx.application.Platform.runLater(() -> {
                        btnLogin.setDisable(false);
                        btnRegister.setDisable(false);

                        if (loginStatus == 1){
                            showAlert("Đăng nhập thành công!", false);
                        }
                        else if (loginStatus == 2){
                            showAlert("Mật khẩu không chính xác!", true);
                            txtPass.clear();
                            txtPass.requestFocus();
                        }
                        else if (loginStatus == 0){
                            showAlert("Tài khoản này không tồn tại!", true);
                            txtUser.clear();
                            txtUser.requestFocus();
                        }
                        else {
                            showAlert("Lỗi kết nối máy chủ dữ liệu!", true);
                        }
                    });
                }).start();
            }
        }
    }

    //EFFECTS
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainForm.setOpacity(0);
        mainForm.setTranslateY(50);

        // Hiệu ứng hiển thị rõ dần
        FadeTransition fade = new FadeTransition(Duration.millis(1500), mainForm);
        fade.setFromValue(0);
        fade.setToValue(1);

        // Hiệu ứng dịch chuyển từ dưới lên
        TranslateTransition translate = new TranslateTransition(Duration.millis(1500), mainForm);
        translate.setFromY(80);
        translate.setToY(0);

        ParallelTransition pt = new ParallelTransition(fade, translate);
        pt.play();
    }

    private void showAlert(String message, boolean isError) {
        Label toast = new Label(message);

        String bgColor = isError ? "#E53935" : "#43A047";
        toast.setStyle("-fx-background-color: " + bgColor + "; " +
                "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; " +
                "-fx-padding: 12 25; -fx-background-radius: 25; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Đưa thông báo vào góc trên cùng ở giữa màn hình
        rootPane.getChildren().add(toast);
        StackPane.setAlignment(toast, Pos.TOP_CENTER);
        StackPane.setMargin(toast, new Insets(40, 0, 0, 0));

        toast.setTranslateY(-50);
        toast.setOpacity(0);

        // Hiệu ứng Hiện ra (Trượt xuống + Rõ dần)
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), toast);
        slideIn.setToY(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), toast);
        fadeIn.setToValue(1);
        ParallelTransition showAnim = new ParallelTransition(slideIn, fadeIn);

        // Hiệu ứng Biến mất (Trượt lên + Mờ dần)
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(400), toast);
        slideOut.setToY(-50);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setToValue(0);
        ParallelTransition hideAnim = new ParallelTransition(slideOut, fadeOut);

        // Đợi 2.5 giây rồi mới chạy hiệu ứng biến mất
        hideAnim.setDelay(Duration.seconds(2.5));
        hideAnim.setOnFinished(e -> rootPane.getChildren().remove(toast));

        SequentialTransition sequence = new SequentialTransition(showAnim, hideAnim);
        sequence.play();
    }
}
