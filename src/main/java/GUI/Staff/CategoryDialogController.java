package GUI.Staff;

import BusinessBLL.CategoryBusiness;
import DataDAL.CategoryData;
import EntityDTO.Category;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CategoryDialogController implements Initializable {

    // ===== FXML COMPONENTS =====
    @FXML private Label     lblTitle;
    @FXML private Label     lblDesc;
    @FXML private TextField txtCategoryName;
    @FXML private Button    btnCancel;
    @FXML private Button    btnSave;

    // ===== DATA =====
    private Category currentCategory = null;
    private boolean  isEditMode      = false;

    // ===== INITIALIZE =====
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Không cần load gì thêm vì form chỉ có 1 trường
    }

    // ===== GỌI TỪ CategoryController KHI MỞ FORM SỬA =====
    public void setCategory(Category category) {
        this.currentCategory = category;
        this.isEditMode      = true;

        // Đổi tiêu đề
        lblTitle.setText("SỬA THÔNG TIN DANH MỤC");
        lblDesc.setText("Chỉnh sửa thông tin danh mục bên dưới.");

        // Điền dữ liệu vào form
        txtCategoryName.setText(category.getCategoryName());
    }

    // ===== LƯU =====
    @FXML
    private void handleSave() {
        // Validate
        String validate = validateInput();
        if (validate != null) {
            showAlert("⚠️ " + validate);
            return;
        }

        if (isEditMode) {
            // ===== SỬA =====
            currentCategory.setCategoryName(txtCategoryName.getText().trim());
            String result = CategoryBusiness.updateCategory(currentCategory);
            if (result.equals("success")) {
                showAlert("✅ Cập nhật danh mục thành công!");
                closeDialog();
            } else {
                showAlert("❌ " + result);
            }
        } else {
            // ===== THÊM MỚI =====
            Category newCategory = new Category(txtCategoryName.getText().trim());
            String result = CategoryBusiness.addCategory(newCategory);
            if (result.equals("success")) {
                showAlert("✅ Thêm danh mục thành công!");
                closeDialog();
            } else {
                showAlert("❌ " + result);
            }
        }
    }

    // ===== HỦY =====
    @FXML
    private void handleCancel() {
        closeDialog();
    }

    // ===== HELPER: Validate =====
    private String validateInput() {
        if (txtCategoryName.getText() == null || txtCategoryName.getText().isBlank())
            return "Vui lòng nhập tên danh mục!";
        return null;
    }

    // ===== HELPER: Đóng dialog =====
    private void closeDialog() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    // ===== HELPER: Hiển thị thông báo =====
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}