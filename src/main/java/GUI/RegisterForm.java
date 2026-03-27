package GUI;

import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.net.*;
import java.util.*;

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
    private PasswordField txtConfirmPassword;

    @FXML
    private TextField txtFullName;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtUsername;

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

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Util.Others.playFormAnimation(mainForm);
    }
}
