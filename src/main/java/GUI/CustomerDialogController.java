package GUI;

import BusinessBLL.CustomerBusiness;
import EntityDTO.Customer;
import Util.Others;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerDialogController implements Initializable {

    @FXML
    private AnchorPane mainPanel;

    @FXML
    private Label lblTitle;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    private Customer currentCustomer = null;
    private boolean saveSuccess = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Chuẩn hóa input giống như Staff
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

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void setCustomerData(Customer customer) {
        this.currentCustomer = customer;

        if(customer != null) {
            lblTitle.setText("SỬA THÔNG TIN KHÁCH HÀNG");
            btnSave.setText("Cập nhật");
            txtPassword.setPromptText("Để trống nếu giữ nguyên mật khẩu cũ");

            txtName.setText(customer.getName());
            txtPhone.setText(customer.getPhone());
            txtUsername.setText(customer.getUser());
        }
    }

    @FXML
    void btnCancelClick(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void btnSaveClick(ActionEvent event) {
        String name = Others.standardizeName(txtName.getText());
        String phone = txtPhone.getText().trim();
        String username = txtUsername.getText().trim();
        String rawPassword = txtPassword.getText().trim();

        // Validate đầu vào
        if (name.isEmpty() || phone.isEmpty() || username.isEmpty() || (currentCustomer == null && rawPassword.isEmpty())) {
            Others.showAlert(mainPanel, "Vui lòng nhập đầy đủ thông tin bắt buộc!", true);
            return;
        }
        else if (!phone.matches("^0[0-9]{9}$")) {
            Others.showAlert(mainPanel, "Số điện thoại không hợp lệ!", true);
            txtPhone.requestFocus();
            return;
        }
        else if (username.length() < 6) {
            Others.showAlert(mainPanel, "Tài khoản phải từ 6 kí tự!", true);
            txtUsername.requestFocus();
            return;
        }
        else if ((currentCustomer == null && rawPassword.length() < 6) ||
                (currentCustomer != null && rawPassword.length() > 0 && rawPassword.length() < 6)) {
            Others.showAlert(mainPanel, "Mật khẩu phải từ 6 kí tự trở lên!", true);
            txtPassword.requestFocus();
            return;
        }

        btnCancel.setDisable(true);
        btnSave.setDisable(true);
        Others.showAlert(mainPanel, "Đang kết nối máy chủ...", false);

        // Chạy đa luồng gọi Database
        new Thread(() -> {
            int status = (currentCustomer == null) ?
                    CustomerBusiness.register(phone, name, username, rawPassword) :
                    CustomerBusiness.updateCustomer(currentCustomer.getId(), phone, name, username, rawPassword, currentCustomer.getPoint());

            Platform.runLater(() -> {
                btnCancel.setDisable(false);
                btnSave.setDisable(false);

                if (status == 1) {
                    saveSuccess = true;
                    Others.showAlert(mainPanel, currentCustomer == null ? "Đã thêm khách hàng thành công!" : "Đã cập nhật thành công!", false);

                    var delay = new PauseTransition(Duration.seconds(2.0));
                    delay.setOnFinished(e -> closeWindow());
                    delay.play();
                } else if (status == -1) {
                    Others.showAlert(mainPanel, "Số điện thoại hoặc Tài khoản đã được sử dụng!", true);
                } else {
                    Others.showAlert(mainPanel, "Lỗi kết nối máy chủ dữ liệu!", true);
                }
            });
        }).start();
    }
}