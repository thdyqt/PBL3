package GUI.Staff;

import BusinessBLL.CategoryBusiness;
import BusinessBLL.ProductBusiness;
import DataDAL.ProductData;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ProductController implements Initializable, IContentArea {
    private StackPane contentArea;
    private ObservableList<Product> masterData = FXCollections.observableArrayList();
    private FilteredList<Product> filteredData;
    private boolean showingInactive = false;
    private Map<Integer, String> categoryCache = new HashMap<>();
    private Map<String, Image> imageCache = new HashMap<>();

    @FXML private Button btnAdd, btnEdit, btnDisable;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbCategory;
    @FXML private TableView<Product> tblProduct;
    @FXML private TableColumn<Product, Integer> colSTT;
    @FXML private TableColumn<Product, String> colProductName, colCategory, colImage;
    @FXML private TableColumn<Product, Integer> colProductPrice, colQuantity;
    @FXML private TableColumn<Product, Boolean> colIsAvailable;
    @FXML private Label lblStatus, lblCount;
    @FXML private CheckBox chkShowInactive;

    @Override
    public void setContentArea(StackPane contentArea) { this.contentArea = contentArea; }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tblProduct.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupTableColumns();
        loadCategories();
        loadProducts();
        setupFiltering();

        if (btnAdd != null) Others.playButtonAnimation(btnAdd);
        if (btnEdit != null) Others.playButtonAnimation(btnEdit);
        if (btnDisable != null) Others.playButtonAnimation(btnDisable);
        Others.animateTableRows(tblProduct);
    }

    private void setupTableColumns() {
        colSTT.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Integer i, boolean e) {
                super.updateItem(i, e);
                setText((e || getTableRow() == null) ? null : String.valueOf(getIndex() + 1));
            }
        });

        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colProductPrice.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        colCategory.setCellValueFactory(c -> {
            String name = categoryCache.getOrDefault(c.getValue().getCategoryID(), "N/A");
            return new javafx.beans.property.SimpleStringProperty(name);
        });

        colIsAvailable.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().isAvailable()));
        colIsAvailable.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item ? "✅ Còn hàng" : "❌ Hết hàng"));
            }
        });

        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        colImage.setCellFactory(c -> new TableCell<>() {
            private final ImageView iv = new ImageView();
            @Override protected void updateItem(String imgName, boolean empty) {
                super.updateItem(imgName, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                iv.setFitWidth(50);
                iv.setFitHeight(50);
                iv.setPreserveRatio(true);

                Others.loadImage(imgName, iv, 50, 50);
                setGraphic(iv);
            }
        });
    }

    private void loadCategories() {
        cbCategory.getItems().setAll("Tất cả danh mục");
        CategoryBusiness.getAllCategories().forEach(c -> {
            cbCategory.getItems().add(c.getCategoryName());
            categoryCache.put(c.getCategoryID(), c.getCategoryName());
        });
        cbCategory.getSelectionModel().selectFirst();
    }

    private void loadProducts() {
        List<Product> listFromDB = showingInactive
                ? ProductData.getInactiveProducts()   // ← lấy sản phẩm Inactive
                : ProductData.getAllProduct();          // ← lấy sản phẩm Active

        masterData.setAll(listFromDB);
        filteredData = new FilteredList<>(masterData, b -> true);
        SortedList<Product> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblProduct.comparatorProperty());
        tblProduct.setItems(sortedData);
        Others.animateTableRows(tblProduct);
        updateStatus(
                showingInactive ? "Sản phẩm đã ngừng kinh doanh" : "Tải dữ liệu thành công!",
                masterData.size()
        );
    }

    private void setupFiltering() {
        txtSearch.textProperty().addListener((o, ov, nv) -> filter());
        cbCategory.valueProperty().addListener((o, ov, nv) -> filter());
    }

    private void filter() {
        String key = txtSearch.getText().toLowerCase().trim();
        String cate = cbCategory.getValue();
        filteredData.setPredicate(p -> {
            boolean matchKey = p.getProductName().toLowerCase().contains(key);
            boolean matchCate = (cate == null || cate.equals("Tất cả danh mục")) ||
                    categoryCache.get(p.getCategoryID()).equals(cate);
            return matchKey && matchCate;
        });
    }

    @FXML private void handleAdd() { openDialog(null); }
    @FXML private void handleEdit() {
        Product s = tblProduct.getSelectionModel().getSelectedItem();
        if (s != null) openDialog(s);
        else Others.showAlert(btnEdit, "Vui lòng chọn sản phẩm!", true);
    }

    private void openDialog(Product p) {
        try {
            FXMLLoader l = new FXMLLoader(getClass().getResource("/GUI/Staff/ProductDialog.fxml"));
            AnchorPane pane = l.load();
            if (p != null) ((ProductDialogController)l.getController()).setProduct(p);
            Stage s = new Stage(); s.setScene(new Scene(pane)); s.showAndWait();
            loadProducts();
        } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML
    private void handleShowInactive() {
        showingInactive = chkShowInactive.isSelected();

        if (showingInactive) {
            // Đổi nút → Bán lại
            btnDisable.setText("✅  Bán lại");
            btnDisable.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #2E7D32, #1B5E20);" +
                            "-fx-text-fill: #C8E6C9; -fx-font-size: 13px; -fx-font-weight: bold;" +
                            "-fx-font-family: 'Serif'; -fx-background-radius: 18;" +
                            "-fx-padding: 0 20 0 20; -fx-cursor: hand;" +
                            "-fx-border-color: #66BB6A; -fx-border-width: 1;" +
                            "-fx-border-radius: 18;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 6, 0, 0, 2);"
            );
            updateStatus("Đang xem sản phẩm đã ngừng kinh doanh", 0);
        } else {
            // Đổi lại nút → Ngừng kinh doanh
            btnDisable.setText("⛔  Ngừng kinh doanh");
            btnDisable.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #7A4A10, #4E2D06);" +
                            "-fx-text-fill: #F5D8A0; -fx-font-size: 13px; -fx-font-weight: bold;" +
                            "-fx-font-family: 'Serif'; -fx-background-radius: 18;" +
                            "-fx-padding: 0 20 0 20; -fx-cursor: hand;" +
                            "-fx-border-color: #C8880A; -fx-border-width: 1;" +
                            "-fx-border-radius: 18;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 6, 0, 0, 2);"
            );
            updateStatus("Đang xem sản phẩm đang kinh doanh", 0);
        }

        // Reload data theo trạng thái
        loadProducts();
    }
    @FXML
    private void handleDisable() {
        Product selected = tblProduct.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(showingInactive
                    ? "⚠️ Vui lòng chọn sản phẩm cần bán lại!"
                    : "⚠️ Vui lòng chọn sản phẩm cần ngừng kinh doanh!"
            );
            return;
        }

        if (showingInactive) {
            // ===== BÁN LẠI =====
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Xác nhận");
            confirm.setHeaderText("Bán lại sản phẩm?");
            confirm.setContentText("Bạn có chắc muốn bán lại: " + selected.getProductName() + "?");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String result = ProductBusiness.restartBusiness(selected.getProductID());
                    if (result.equals("success")) {
                        showAlert("✅ Bán lại thành công!");
                        loadProducts();
                    } else {
                        showAlert("❌ " + result);
                    }
                }
            });

        } else {
            // ===== NGỪNG KINH DOANH =====
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Xác nhận");
            confirm.setHeaderText("Ngừng kinh doanh sản phẩm?");
            confirm.setContentText("Bạn có chắc muốn ngừng kinh doanh: " + selected.getProductName() + "?");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String result = ProductBusiness.stopBusiness(selected.getProductID());
                    if (result.equals("success")) {
                        showAlert("✅ Ngừng kinh doanh thành công!");
                        loadProducts();
                    } else {
                        showAlert("❌ " + result);
                    }
                }
            });
        }
    }

    @FXML private void handleBack() {
        if (contentArea != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Staff/ProductMenu.fxml"));
                Node view = loader.load();

                Object controller = loader.getController();
                if (controller instanceof IContentArea) {
                    ((IContentArea) controller).setContentArea(contentArea);
                }

                contentArea.getChildren().setAll(view);

            } catch (IOException e) {
                System.err.println("❌ Lỗi khi quay lại màn hình trước: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("⚠️ Cảnh báo: contentArea đang bị Null, không thể chuyển trang!");
        }
    }
    // ===== HELPER: Cập nhật status bar =====
    private void updateStatus(String message, int count) {
        lblStatus.setText(message);
        lblCount.setText(count + " sản phẩm");
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