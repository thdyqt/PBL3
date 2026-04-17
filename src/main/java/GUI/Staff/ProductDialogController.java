package GUI.Staff;

import BusinessBLL.CategoryBusiness;
import BusinessBLL.ProductBusiness;
import EntityDTO.Category;
import EntityDTO.Product;
import Util.Others;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class ProductDialogController implements Initializable {

    // ==========================================
    // 1. KHAI BÁO CÁC THÀNH PHẦN GIAO DIỆN (FXML)
    // ==========================================
    @FXML private Label lblTitle;
    @FXML private Label lblDesc;

    @FXML private TextField txtProductName;
    @FXML private TextField txtPrice;
    @FXML private TextField txtQuantity;

    @FXML private TextArea txtDescription;
    @FXML private TextArea txtIngredients;

    @FXML private ComboBox<String> cbCategory;
    @FXML private ImageView imgPreview;

    @FXML private Button btnChooseImage;
    @FXML private Button btnCancel;
    @FXML private Button btnSave;

    // ==========================================
    // 2. KHAI BÁO BIẾN TOÀN CỤC
    // ==========================================
    private Product currentProduct = null;
    private String selectedImageName = null;
    private boolean isEditMode = false;

    // ==========================================
    // 3. HÀM CHẠY ĐẦU TIÊN KHI MỞ FORM
    // ==========================================
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupInputValidation();
        loadCategoriesToComboBox();
    }

    // Ràng buộc người dùng nhập đúng kiểu dữ liệu
    private void setupInputValidation() {
        Others.setMaxLength(txtProductName, 50);
        Others.setMaxLength(txtPrice, 10);
        Others.setMaxLength(txtQuantity, 3);

        // Chỉ cho phép nhập SỐ vào ô Giá
        txtPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtPrice.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Chỉ cho phép nhập SỐ vào ô Số lượng
        txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtQuantity.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    // Tải danh sách danh mục từ Database lên ComboBox
    private void loadCategoriesToComboBox() {
        List<Category> categories = CategoryBusiness.getAllCategories();
        for (Category category : categories) {
            cbCategory.getItems().add(category.getCategoryName());
        }
    }

    // ==========================================
    // 4. NHẬN DỮ LIỆU TỪ MÀN HÌNH CHÍNH (KHI BẤM NÚT SỬA)
    // ==========================================
    public void setProduct(Product product) {
        this.currentProduct = product;
        this.isEditMode = true;

        // Đổi tiêu đề
        lblTitle.setText("SỬA THÔNG TIN SẢN PHẨM");
        lblDesc.setText("Chỉnh sửa thông tin sản phẩm bên dưới.");

        // Đổ dữ liệu cũ vào các ô nhập liệu
        txtProductName.setText(product.getProductName());
        txtPrice.setText(String.valueOf(product.getProductPrice()));
        txtQuantity.setText(String.valueOf(product.getQuantity()));
        txtDescription.setText(product.getDescription());
        txtIngredients.setText(product.getIngredients());

        // Chọn đúng Danh mục cũ
        Category category = CategoryBusiness.getCategoryByID(product.getCategoryID());
        if (category != null) {
            cbCategory.setValue(category.getCategoryName());
        }

        // Hiển thị ảnh cũ
        selectedImageName = product.getImage();
        updateImagePreview(selectedImageName);
    }

    // Hàm hỗ trợ hiển thị ảnh
    private void updateImagePreview(String imageName) {
        try {
            if (imageName == null || imageName.isEmpty()) {
                imageName = "default.png";
            }
            InputStream imageStream = getClass().getResourceAsStream("/images/" + imageName);
            if (imageStream != null) {
                imgPreview.setImage(new Image(imageStream));
            }
        } catch (Exception e) {
            System.out.println("Không tìm thấy ảnh: " + imageName);
        }
    }

    // ==========================================
    // 5. CHỨC NĂNG CHỌN ẢNH TỪ MÁY TÍNH
    // ==========================================
    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh sản phẩm");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Hình ảnh (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg")
        );

        // Mở hộp thoại chọn file
        File selectedFile = fileChooser.showOpenDialog(btnChooseImage.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Xóa khoảng trắng trong tên file để tránh lỗi URL
                selectedImageName = selectedFile.getName().replace(" ", "_");

                // Copy file ảnh vào thư mục resources/images/ của project
                URL resourceURL = getClass().getResource("/images/");
                File imageDirectory = new File(resourceURL.toURI());
                File destinationFile = new File(imageDirectory, selectedImageName);

                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Cập nhật ảnh lên giao diện
                imgPreview.setImage(new Image(selectedFile.toURI().toString()));

            } catch (Exception e) {
                Others.showAlert(btnChooseImage, "Lỗi khi lưu ảnh vào hệ thống!", true);
                e.printStackTrace();
            }
        }
    }

    // ==========================================
    // 6. CHỨC NĂNG LƯU DỮ LIỆU
    // ==========================================
    @FXML
    private void handleSave() {
        // Bước 1: Kiểm tra xem người dùng đã nhập đủ thông tin chưa
        if (txtProductName.getText().trim().isEmpty() ||
                cbCategory.getValue() == null ||
                txtPrice.getText().trim().isEmpty() ||
                txtQuantity.getText().trim().isEmpty()) {

            Others.showAlert(btnSave, "Vui lòng nhập đầy đủ các thông tin bắt buộc!", true);
            return;
        }

        // Bước 2: Lấy dữ liệu từ giao diện
        String productName = txtProductName.getText().trim();
        int categoryID = CategoryBusiness.getCategoryIDByName(cbCategory.getValue());
        int price = Integer.parseInt(txtPrice.getText().trim());
        int quantity = Integer.parseInt(txtQuantity.getText().trim());
        String description = txtDescription.getText().trim();
        String ingredients = txtIngredients.getText().trim();

        // Bước 3: Chuẩn bị Đối tượng Product
        Product productToSave;

        if (isEditMode) {
            // Đang sửa -> Lấy sản phẩm cũ và cập nhật lại thông tin
            productToSave = currentProduct;
            productToSave.setProductName(productName);
            productToSave.setCategoryID(categoryID);
            productToSave.setProductPrice(price);
            productToSave.setQuantity(quantity);
            productToSave.setDescription(description);
            productToSave.setIngredients(ingredients);
            productToSave.setImage(selectedImageName);
        } else {
            // Đang thêm mới -> Tạo sản phẩm mới hoàn toàn
            productToSave = new Product(
                    productName,
                    categoryID,
                    price,
                    quantity,
                    "Active",     // Trạng thái mặc định
                    description,
                    ingredients,
                    5.0,          // Đánh giá mặc định cho sản phẩm mới
                    selectedImageName
            );
        }

        // Bước 4: Gọi tầng Business để lưu vào Database
        String result;
        if (isEditMode) {
            result = ProductBusiness.updateProduct(productToSave);
        } else {
            result = ProductBusiness.addProduct(productToSave);
        }

        // Bước 5: Xử lý kết quả trả về
        if (result.equals("success")) {
            Others.showAlert(btnSave, "Lưu thông tin sản phẩm thành công!", false);

            // Delay 0.8 giây để người dùng kịp đọc thông báo rồi tự động đóng cửa sổ
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> closeDialog());
                }
            }, 800);

        } else {
            // Hiện lỗi (Ví dụ: Sản phẩm đã tồn tại)
            Others.showAlert(btnSave, result, true);
        }
    }

    // ==========================================
    // 7. CHỨC NĂNG HỦY / ĐÓNG CỬA SỔ
    // ==========================================
    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}