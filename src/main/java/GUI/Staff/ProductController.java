package GUI.Staff;

import BusinessBLL.CategoryBusiness;
import BusinessBLL.ProductBusiness;
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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ProductController implements Initializable, IContentArea {
    private StackPane contentArea;
    private ObservableList<Product> masterData = FXCollections.observableArrayList();
    private FilteredList<Product> filteredData;

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

    @Override
    public void setContentArea(StackPane contentArea) { this.contentArea = contentArea; }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tblProduct.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupTableColumns();
        loadCategories();
        loadProducts();
        setupFiltering();
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
                if (empty) { setGraphic(null); return; }
                iv.setFitWidth(50); iv.setFitHeight(50); iv.setPreserveRatio(true);
                String path = (imgName == null || imgName.isEmpty()) ? "default.png" : imgName;
                if (!imageCache.containsKey(path)) {
                    try {
                        String url = getClass().getResource("/images/" + path).toExternalForm();
                        imageCache.put(path, new Image(url, 50, 50, true, true, true));
                    } catch (Exception e) { imageCache.put(path, null); }
                }
                iv.setImage(imageCache.get(path));
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
        masterData.setAll(ProductBusiness.getAllProducts());
        filteredData = new FilteredList<>(masterData, p -> true);
        SortedList<Product> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblProduct.comparatorProperty());
        tblProduct.setItems(sortedData);
        lblCount.setText(masterData.size() + " sản phẩm");
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

    @FXML private void handleDisable() {
        Product s = tblProduct.getSelectionModel().getSelectedItem();
        if (s != null && Others.showCustomConfirm("Xác nhận", "Ngừng bán " + s.getProductName() + "?", "Đồng ý", "Hủy")) {
            if (ProductBusiness.stopBusiness(s.getProductID()).equals("success")) {
                Others.showAlert(btnDisable, "Thành công!", false);
                loadProducts();
            }
        }
    }
    private void switchForm(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Node node = loader.load();
            Object controller = loader.getController();
            if (controller instanceof IContentArea ctrl) {
                ctrl.setContentArea(this.contentArea);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(node);
        } catch (Exception e) {
            System.out.println("Lỗi khi tải trang: " + fxmlFileName);
            e.printStackTrace();
        }
    }
    @FXML private void handleBack() {
        switchForm("ProductMenu.fxml");
    }
}