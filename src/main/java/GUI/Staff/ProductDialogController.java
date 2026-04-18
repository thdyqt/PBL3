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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class ProductDialogController implements Initializable {
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

    private Product currentProduct = null;
    private String selectedImageName = null;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupInputValidation();
        loadCategoriesToComboBox();
    }

    private void setupInputValidation() {
        Others.setMaxLength(txtProductName, 50);
        Others.setMaxLength(txtPrice, 10);
        Others.setMaxLength(txtQuantity, 3);

        txtPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtPrice.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtQuantity.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void loadCategoriesToComboBox() {
        List<Category> categories = CategoryBusiness.getAllCategories();
        for (Category category : categories) {
            cbCategory.getItems().add(category.getCategoryName());
        }
    }

    public void setProduct(Product product) {
        this.currentProduct = product;
        this.isEditMode = true;

        lblTitle.setText("SỬA THÔNG TIN SẢN PHẨM");
        lblDesc.setText("Chỉnh sửa thông tin sản phẩm bên dưới.");

        txtProductName.setText(product.getProductName());
        txtPrice.setText(String.valueOf(product.getProductPrice()));
        txtQuantity.setText(String.valueOf(product.getQuantity()));
        txtDescription.setText(product.getDescription());
        txtIngredients.setText(product.getIngredients());

        Category category = CategoryBusiness.getCategoryByID(product.getCategoryID());
        if (category != null) {
            cbCategory.setValue(category.getCategoryName());
        }

        selectedImageName = product.getImage();
        Others.loadImage(selectedImageName, imgPreview, 250, 250);
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh sản phẩm");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Hình ảnh (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(btnChooseImage.getScene().getWindow());

        if (selectedFile != null) {
            try {
                selectedImageName = selectedFile.getName().replace(" ", "_");
                URL resourceURL = getClass().getResource("/images/");
                File imageDirectory = new File(resourceURL.toURI());
                File destinationFile = new File(imageDirectory, selectedImageName);

                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                imgPreview.setImage(new Image(selectedFile.toURI().toString()));

            } catch (Exception e) {
                Others.showAlert(btnChooseImage, "Lỗi khi lưu ảnh vào hệ thống!", true);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSave() {
        if (txtProductName.getText().trim().isEmpty() ||
                cbCategory.getValue() == null ||
                txtPrice.getText().trim().isEmpty() ||
                txtQuantity.getText().trim().isEmpty()) {

            Others.showAlert(btnSave, "Vui lòng nhập đầy đủ các thông tin bắt buộc!", true);
            return;
        }

        String productName = txtProductName.getText().trim();
        int categoryID = CategoryBusiness.getCategoryIDByName(cbCategory.getValue());
        int price = Integer.parseInt(txtPrice.getText().trim());
        int quantity = Integer.parseInt(txtQuantity.getText().trim());
        String description = txtDescription.getText().trim();
        String ingredients = txtIngredients.getText().trim();

        Product productToSave;

        if (isEditMode) {
            productToSave = currentProduct;
            productToSave.setProductName(productName);
            productToSave.setCategoryID(categoryID);
            productToSave.setProductPrice(price);
            productToSave.setQuantity(quantity);
            productToSave.setDescription(description);
            productToSave.setIngredients(ingredients);
            productToSave.setImage(selectedImageName);
        } else {
            productToSave = new Product(
                    productName,
                    categoryID,
                    price,
                    quantity,
                    "Active",
                    description,
                    ingredients,
                    5.0,
                    selectedImageName
            );
        }

        String result;
        if (isEditMode) {
            result = ProductBusiness.updateProduct(productToSave);
        } else {
            result = ProductBusiness.addProduct(productToSave);
        }

        if (result.equals("success")) {
            Others.showAlert(btnSave, "Lưu thông tin sản phẩm thành công!", false);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> closeDialog());
                }
            }, 800);

        } else {
            Others.showAlert(btnSave, result, true);
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}