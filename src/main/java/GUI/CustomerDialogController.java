package GUI;

import Data.CustomerData;
import Entity.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// Import thư viện BCrypt để mã hóa mật khẩu
import org.mindrot.jbcrypt.BCrypt;

public class CustomerDialogController {

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

    @FXML
    void btnCancelClick(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void btnSaveClick(ActionEvent event) {
        // Lấy dữ liệu người dùng nhập
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String username = txtUsername.getText().trim();
        String rawPassword = txtPassword.getText().trim(); // Lấy mật khẩu thô

        // 1. Kiểm tra không được để trống
        if (name.isEmpty() || phone.isEmpty() || username.isEmpty() || rawPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ tất cả các trường!");
            return;
        }

        // 2. Kiểm tra tài khoản hoặc SĐT đã tồn tại chưa
        if (CustomerData.isAccountExist(username, phone)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi dữ liệu", "Tài khoản hoặc Số điện thoại này đã được sử dụng!");
            return;
        }

        // 3. MÃ HÓA MẬT KHẨU BẰNG BCRYPT
        // BCrypt.gensalt() sẽ tự động tạo ra một chuỗi salt ngẫu nhiên để tăng cường độ khó giải mã
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        // 4. Tạo đối tượng Customer mới với mật khẩu ĐÃ MÃ HÓA
        Customer newCustomer = new Customer(phone, name, username, hashedPassword);

        // 5. Gọi DB để lưu trữ
        boolean isSuccess = CustomerData.addCustomer(newCustomer);

        if (isSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm khách hàng mới thành công!");
            closeWindow(); // Đóng form thêm sau khi lưu thành công
        } else {
            showAlert(Alert.AlertType.ERROR, "Thất bại", "Đã xảy ra lỗi khi thêm vào cơ sở dữ liệu. Vui lòng thử lại!");
        }
    }

    // Hàm phụ trợ dùng để đóng cửa sổ hiện tại
    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    // Hàm phụ trợ dùng để hiển thị thông báo (Alert)
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}