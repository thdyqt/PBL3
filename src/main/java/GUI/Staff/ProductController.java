package GUI.Staff;

import DataDAL.ProductData;
import DataDAL.CategoryData;
import DataDAL.ProductData;
import DataDAL.ProductData;
import EntityDTO.Category;
import EntityDTO.Product;
import Util.IContentArea;
import Util.Others;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProductController implements Initializable, IContentArea {
    private StackPane contentArea;

    @Override
    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    // ===== FXML COMPONENTS =====
    @FXML private Button btnBack;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDisable;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbCategory;

    @FXML private TableView<Product> tblProduct;
    @FXML private TableColumn<Product, Integer> colSTT;
    @FXML private TableColumn<Product, Integer> colProductID;
    @FXML private TableColumn<Product, String>  colProductName;
    @FXML private TableColumn<Product, String>  colCategoryID;
    @FXML private TableColumn<Product, Integer> colProductPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, Boolean> colIsAvailable;
    @FXML private TableColumn<Product, String>  colImage;

    @FXML private Label lblStatus;
    @FXML private Label lblCount;

    // ===== DATA =====

    private ObservableList<Product> masterData = FXCollections.observableArrayList();
    private FilteredList<Product> filteredData;

    // ===== INITIALIZE =====
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadCategories();
        loadProducts();
        setupSearch();
        setupCategoryFilter();
    }

    private void setupTableColumns() {

        // STT
        colSTT.setCellValueFactory(col ->
                new javafx.beans.property.SimpleIntegerProperty(
                        tblProduct.getItems().indexOf(col.getValue()) + 1
                ).asObject()
        );

        // Các cột đơn giản
        colProductID.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getProductID())
        );
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colProductPrice.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Danh mục — hiển thị tên thay vì ID
        colCategoryID.setCellValueFactory(col -> {
            try {
                int categoryID = col.getValue().getCategoryID();
                Category c = CategoryData.getByID(categoryID);
                String name = c != null ? c.getCategoryName() : "Không rõ";
                return new javafx.beans.property.SimpleStringProperty(name);
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Lỗi");
            }
        });

        // Trạng thái tồn kho
        colIsAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));
        colIsAvailable.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "✅ Còn hàng" : "❌ Hết hàng");
                }
            }
        });

        // Hình ảnh — thêm try-catch tránh crash cả bảng
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        colImage.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                try {
                    // Thử load ảnh sản phẩm
                    var stream = item != null
                            ? getClass().getResourceAsStream("/images/" + item)
                            : null;

                    // Nếu không có → dùng ảnh mặc định
                    if (stream == null) {
                        stream = getClass().getResourceAsStream("/images/default.png");
                    }

                    if (stream != null) {
                        imageView.setImage(new Image(stream));
                        setGraphic(imageView);
                    } else {
                        setText("No image");
                        setGraphic(null);
                    }
                } catch (Exception e) {
                    setText("Lỗi ảnh");
                    setGraphic(null);
                }
            }
        });
    }

    // ===== LOAD CATEGORIES VÀO COMBOBOX =====
    private void loadCategories() {
        cbCategory.getItems().clear();
        cbCategory.getItems().add("Tất cả danh mục");

        List<Category> categories = CategoryData.getAll();

        System.out.println("Số danh mục load được: " + categories.size());
        for (Category c : categories) {
            cbCategory.getItems().add(c.getCategoryName());
        }
        cbCategory.getSelectionModel().selectFirst();
    }

    // ===== LOAD TẤT CẢ SẢN PHẨM =====
    private void loadProducts() {
        List<Product> listFromDB = ProductData.getAllProduct();
        // ===== DEBUG =====
        for (Product p : listFromDB) {
            System.out.println("ID: " + p.getProductID() + " | " + p.getProductName());
        }
        //
        masterData.setAll(listFromDB);
        filteredData = new FilteredList<>(masterData, b -> true);
        SortedList<Product> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblProduct.comparatorProperty());
        tblProduct.setItems(sortedData);
        Others.animateTableRows(tblProduct);  // nếu dùng chung class Others
        updateStatus("Tải dữ liệu thành công!", masterData.size());
    }

    // ===== TÌM KIẾM THEO TÊN =====
    private void setupSearch() {
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filterProducts();
        });
    }

    // ===== LỌC THEO DANH MỤC =====
    private void setupCategoryFilter() {
        cbCategory.valueProperty().addListener((obs, oldVal, newVal) -> {
            filterProducts();
        });
    }

    // ===== LỌC SẢN PHẨM KẾT HỢP TÊN + DANH MỤC =====
    private void filterProducts() {
        String keyword     = txtSearch.getText().toLowerCase().trim();
        String categorySelected = cbCategory.getValue();

        List<Product> all = ProductData.getAllProduct();
        ObservableList<Product> filtered = FXCollections.observableArrayList();

        for (Product p : all) {
            boolean matchName = p.getProductName().toLowerCase().contains(keyword);

            boolean matchCategory = true;
            if (categorySelected != null && !categorySelected.equals("Tất cả danh mục")) {
                Category c = CategoryData.getByID(p.getCategoryID());
                matchCategory = c != null && c.getCategoryName().equals(categorySelected);
            }

            if (matchName && matchCategory) filtered.add(p);
        }

        tblProduct.setItems(filtered);
        updateStatus("Kết quả tìm kiếm", filtered.size());
    }
    private void openDialog(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/GUI/Staff/ProductDialog.fxml")
            );
            AnchorPane pane = loader.load();

            // Nếu là sửa → truyền dữ liệu vào controller
            if (product != null) {
                ProductDialogController controller = loader.getController();
                controller.setProduct(product);
            }

            Stage stage = new Stage();
            stage.setTitle(product == null ? "Thêm sản phẩm" : "Sửa sản phẩm");
            stage.setScene(new Scene(pane));
            stage.setResizable(false);
            stage.showAndWait();  // Chờ đóng dialog rồi reload bảng

            loadProducts();  // Reload lại bảng sau khi đóng dialog

        } catch (Exception e) {
            System.err.println("Lỗi mở dialog: " + e.getMessage());
        }
    }

    // ===== XỬ LÝ NÚT THÊM =====
    @FXML
    private void handleAdd() {

        lblStatus.setText("Mở form thêm sản phẩm...");
        openDialog(null);
    }

    // ===== XỬ LÝ NÚT SỬA =====
    @FXML
    private void handleEdit() {
        Product selected = tblProduct.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("⚠️ Vui lòng chọn sản phẩm cần sửa!");
            return;
        }
       openDialog(selected);
        lblStatus.setText("Mở form sửa: " + selected.getProductName());
    }

    // ===== XỬ LÝ NÚT NGỪNG KINH DOANH =====
    @FXML
    private void handleDisable() {
        Product selected = tblProduct.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("⚠️ Vui lòng chọn sản phẩm cần ngừng kinh doanh!");
            return;
        }

        // Xác nhận trước khi ngừng
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Ngừng kinh doanh sản phẩm?");
        confirm.setContentText("Bạn có chắc muốn ngừng kinh doanh: "
                + selected.getProductName() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean result = DataDAL.ProductData.stopBusiness(selected.getProductID());
                if (result) {
                    showAlert("✅ Ngừng kinh doanh thành công!");
                    loadProducts();  // Reload lại bảng
                } else {
                    showAlert("❌  Ngừng kinh doanh thất bại, vui lòng thử lại!");
                }
            }
        });
    }

    // ===== XỬ LÝ NÚT QUAY LẠI =====




    // ===== HELPER: Cập nhật status bar =====
    private void updateStatus(String message, int count) {
        lblStatus.setText(message);
        lblCount.setText(count + " sản phẩm");
    }
    @FXML
    private void handleBack() {
        switchForm("/GUI/Staff/productMenu.fxml");
    }

    private void switchForm(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Node node = loader.load();
            Object ctrl = loader.getController();
            if (ctrl instanceof IContentArea ic) {
                ic.setContentArea(this.contentArea);
            }
            contentArea.getChildren().clear();
            contentArea.getChildren().add(node);
        } catch (Exception e) {
            System.out.println("Lỗi khi tải trang: " + fxmlFileName);
            e.printStackTrace();
        }
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