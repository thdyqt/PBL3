package GUI.Staff;

import DataDAL.ProductData;
import DataDAL.CategoryData;

import EntityDTO.Category;
import EntityDTO.Product;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;

public class ProductDialogController implements Initializable {

    // ===== FXML COMPONENTS =====
    @FXML private Label      lblTitle;
    @FXML private Label      lblDesc;
    @FXML private TextField  txtProductName;
    @FXML private ComboBox<String> cbCategory;
    @FXML private TextField  txtPrice;
    @FXML private TextField  txtQuantity;
    @FXML private ImageView  imgPreview;
    @FXML private Button     btnChooseImage;
    @FXML private Button     btnCancel;
    @FXML private Button     btnSave;

    // ===== DATA =====
    private Product currentProduct = null;   // null = thêm mới, có data = sửa
    private String  selectedImageName = null;
    private boolean isEditMode = false;
    private boolean imageChanged = false;
    // ===== INITIALIZE =====
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCategories();
        loadDefaultImage();
    }

    // ===== LOAD DANH MỤC VÀO COMBOBOX =====
    private void loadCategories() {
        cbCategory.getItems().clear();
        List<Category> categories = CategoryData.getAll();
        for (Category c : categories) {
            cbCategory.getItems().add(c.getCategoryName());
        }
    }

    // ===== LOAD ẢNH MẶC ĐỊNH =====
    private void loadDefaultImage() {
        try {
            Image defaultImage = new Image(
                    getClass().getResourceAsStream("/images/default.png")
            );
            imgPreview.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Không tìm thấy ảnh mặc định!");
        }
    }

    // ===== GỌI TỪ ProductController KHI MỞ FORM SỬA =====
    public void setProduct(Product product) {
        this.currentProduct = product;
        this.isEditMode     = true;
        this.imageChanged   = false;
        // Đổi tiêu đề
        lblTitle.setText("SỬA THÔNG TIN SẢN PHẨM");
        lblDesc.setText("Chỉnh sửa thông tin sản phẩm bên dưới.");

        // Điền dữ liệu vào form
        txtProductName.setText(product.getProductName());
        txtPrice.setText(String.valueOf(product.getProductPrice()));
        txtQuantity.setText(String.valueOf(product.getQuantity()));

        // Chọn đúng danh mục trong ComboBox
        Category c = CategoryData.getByID(product.getCategoryID());
        if (c != null) cbCategory.setValue(c.getCategoryName());

        // Load ảnh hiện tại
        selectedImageName = product.getImage();
        if (selectedImageName != null && !selectedImageName.isBlank()) {
            try {
                Image image = new Image(
                        getClass().getResourceAsStream("/images/" + selectedImageName)
                );
                imgPreview.setImage(image);
            } catch (Exception e) {
                loadDefaultImage();
            }
        }
    }

    // ===== CHỌN ẢNH =====
    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh sản phẩm");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(btnChooseImage.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Tên file bỏ dấu cách
                selectedImageName = selectedFile.getName().replace(" ", "_");
                imageChanged = true;
                // ✅ Sửa lại cách lấy đường dẫn thư mục images
                URL resourceURL = getClass().getResource("/images/");
                File imageDir = new File(resourceURL.toURI());  // ← dùng toURI() thay vì getPath()
                File destination = new File(imageDir, selectedImageName);
                // Copy file vào thư mục images
                Files.copy(selectedFile.toPath(), destination.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                // Preview ảnh
                Image image = new Image(selectedFile.toURI().toString());
                imgPreview.setImage(image);

            } catch (Exception e) {
                showAlert("❌ Lỗi khi chọn ảnh: " + e.getMessage());
            }
        }
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
        boolean isA = true;
        // Lấy CategoryID từ tên danh mục đã chọn
        String categoryName = cbCategory.getValue();
        int categoryID = getCategoryIDByName(categoryName);

        if (isEditMode) {
            // ===== SỬA =====
            if (selectedImageName == null) {
                selectedImageName = currentProduct.getImage();
            }
            currentProduct.setImage(selectedImageName);
            currentProduct.setProductName(txtProductName.getText().trim());
            currentProduct.setCategoryID(categoryID);
            currentProduct.setProductPrice(Integer.parseInt(txtPrice.getText().trim()));
            currentProduct.setQuantity(Integer.parseInt(txtQuantity.getText().trim()));
            currentProduct.setAvailable(isA);

           boolean result = ProductData.updateProduct(currentProduct);
            if (result ) {
                showAlert("✅ Cập nhật sản phẩm thành công!");
                closeDialog();
            } else {
                showAlert("❌ " + result);
            }

        } else {
            // ===== THÊM MỚI =====
            Product newProduct = new Product(
                    txtProductName.getText().trim(),
                    categoryID,
                    Integer.parseInt(txtPrice.getText().trim()),
                    Integer.parseInt(txtQuantity.getText().trim()),
                    isA,
                    selectedImageName
            );

           boolean result = ProductData.addProduct(newProduct);
            if (result) {
                showAlert("✅ Thêm sản phẩm thành công!");
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

    // ===== HELPER: Validate input =====
    private String validateInput() {
        if (txtProductName.getText().isBlank())
            return "Vui lòng nhập tên sản phẩm!";
        if (cbCategory.getValue() == null)
            return "Vui lòng chọn danh mục!";
        if (txtPrice.getText().isBlank())
            return "Vui lòng nhập giá sản phẩm!";
        try {
            int price = Integer.parseInt(txtPrice.getText().trim());
            if (price < 0) return "Giá không được âm!";
        } catch (NumberFormatException e) {
            return "Giá phải là số nguyên!";
        }
        if (txtQuantity.getText().isBlank())
            return "Vui lòng nhập số lượng!";
        try {
            int qty = Integer.parseInt(txtQuantity.getText().trim());
            if (qty < 0) return "Số lượng không được âm!";
        } catch (NumberFormatException e) {
            return "Số lượng phải là số nguyên!";
        }
        return null; // Hợp lệ
    }

    // ===== HELPER: Lấy CategoryID từ tên =====
    private int getCategoryIDByName(String categoryName) {
        List<Category> categories = CategoryData.getAll();
        for (Category c : categories) {
            if (c.getCategoryName().equals(categoryName)) {
                return c.getCategoryID();
            }
        }
        return -1;
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