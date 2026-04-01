package GUI;

import Data.CustomerData;
import Entity.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.mindrot.jbcrypt.BCrypt;

public class CustomerDialogController {

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

    private Customer currentCustomer; // Khách hàng đang được chọn để sửa
    private boolean isEditMode = false; // Cờ đánh dấu chế độ Thêm hay Sửa

    // Hàm nhận dữ liệu từ form Quản lý truyền sang khi ấn nút Sửa
    public void setCustomerData(Customer customer) {
        this.currentCustomer = customer;
        this.isEditMode = true;

        // Đổi giao diện sang chế độ Sửa
        lblTitle.setText("SỬA THÔNG TIN KHÁCH HÀNG");
        btnSave.setText("Cập nhật");
        txtPassword.setPromptText("Để trống nếu giữ nguyên mật khẩu cũ");

        // Đổ dữ liệu cũ vào các ô Text
        txtName.setText(customer.getName());
        txtPhone.setText(customer.getPhone());
        txtUsername.setText(customer.getUser());
    }

    @FXML
    void btnCancelClick(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void btnSaveClick(ActionEvent event) {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String username = txtUsername.getText().trim();
        String rawPassword = txtPassword.getText().trim();

        // 1. Kiểm tra không được để trống thông tin cơ bản
        if (name.isEmpty() || phone.isEmpty() || username.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ Tên, SĐT và Tài khoản!");
            return;
        }

        if (isEditMode) {
            // ----- XỬ LÝ SỬA -----
            currentCustomer.setName(name);
            currentCustomer.setPhone(phone);
            currentCustomer.setUser(username);

            // Nếu người dùng nhập mật khẩu mới thì tiến hành Hash và đổi
            if (!rawPassword.isEmpty()) {
                currentCustomer.setPassword(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
            }

            if (CustomerData.updateCustomer(currentCustomer)) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin khách hàng!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Thất bại", "Cập nhật thất bại. Số điện thoại hoặc Tài khoản có thể bị trùng!");
            }

        } else {
            // ----- XỬ LÝ THÊM MỚI -----
            if (rawPassword.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập mật khẩu cho khách hàng mới!");
                return;
            }

            if (CustomerData.isAccountExist(username, phone)) {
                showAlert(Alert.AlertType.ERROR, "Lỗi dữ liệu", "Tài khoản hoặc Số điện thoại này đã được sử dụng!");
                return;
            }

            String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
            Customer newCustomer = new Customer(phone, name, username, hashedPassword);

            if (CustomerData.addCustomer(newCustomer)) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm khách hàng mới thành công!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Thất bại", "Đã xảy ra lỗi khi thêm vào cơ sở dữ liệu!");
            }
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}