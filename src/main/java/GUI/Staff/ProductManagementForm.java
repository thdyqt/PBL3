package GUI.Staff;

import BusinessBLL.CategoryBusiness;
import BusinessBLL.ProductBusiness;
import DataDAL.ProductData;
import EntityDTO.Product;
import Util.Others;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ProductManagementForm implements Initializable {
    private ObservableList<Product> masterData = FXCollections.observableArrayList();
    private FilteredList<Product> filteredData;
    private boolean showingInactive = false;
    private Map<Integer, String> categoryCache = new HashMap<>();

    @FXML private BorderPane mainPane;
    @FXML private Button btnBack, btnAdd, btnEdit, btnDisable;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbCategory;
    @FXML private TableView<Product> tblProduct;
    @FXML private TableColumn<Product, Integer> colSTT, colProductPrice, colQuantity;
    @FXML private TableColumn<Product, String> colProductName, colCategory, colImage;
    @FXML private TableColumn<Product, Boolean> colIsAvailable;
    @FXML private Label lblStatus, lblCount;
    @FXML private CheckBox chkShowInactive;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tblProduct.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupTableColumns();
        loadCategories();
        loadProducts();
        setupFiltering();

        tblProduct.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Product selectedProduct = tblProduct.getSelectionModel().getSelectedItem();
                if (selectedProduct != null) {
                    openDialog(selectedProduct);
                }
            }
        });

        if (btnAdd != null) Others.playButtonAnimation(btnAdd);
        if (btnEdit != null) Others.playButtonAnimation(btnEdit);
        if (btnDisable != null) Others.playButtonAnimation(btnDisable);
        if (btnBack != null) Others.playButtonAnimation(btnBack);

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
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        colProductPrice.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        colProductPrice.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Integer price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(Others.formatPrice(price));
                }
            }
        });

        colCategory.setCellValueFactory(c -> {
            String name = categoryCache.getOrDefault(c.getValue().getCategoryID(), "N/A");
            return new javafx.beans.property.SimpleStringProperty(name);
        });

        colIsAvailable.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().isAvailable()));
        colIsAvailable.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (item) {
                        setText("✅ Còn hàng");
                        setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setText("❌ Hết hàng");
                        setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    }
                }
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
                ? ProductData.getInactiveProducts()
                : ProductData.getAllProduct();

        masterData.setAll(listFromDB);
        filteredData = new FilteredList<>(masterData, b -> true);
        SortedList<Product> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblProduct.comparatorProperty());
        tblProduct.setItems(sortedData);
        Others.animateTableRows(tblProduct);
        updateStatus(
                showingInactive ? "Đang xem sản phẩm đã ngừng kinh doanh" : "Đang xem sản phẩm đang kinh doanh",
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
            boolean matchCate = (cate == null || cate.equals("Tất cả danh mục")) ||
                    categoryCache.getOrDefault(p.getCategoryID(), "").equals(cate);

            if (!matchCate) return false;
            if (key.isEmpty()) return true;

            String cateName = categoryCache.getOrDefault(p.getCategoryID(), "").toLowerCase();
            String statusStr = p.isAvailable() ? "còn hàng" : "hết hàng";
            String priceStr = String.valueOf(p.getProductPrice());
            String priceFormatted = Others.formatPrice(p.getProductPrice()).toLowerCase();
            String qtyStr = String.valueOf(p.getQuantity());

            return p.getProductName().toLowerCase().contains(key) ||
                    cateName.contains(key) ||
                    priceStr.contains(key) ||
                    priceFormatted.contains(key) ||
                    qtyStr.contains(key) ||
                    statusStr.contains(key);
        });
    }

    @FXML private void handleAdd() { openDialog(null); }

    @FXML private void handleEdit() {
        Product s = tblProduct.getSelectionModel().getSelectedItem();
        if (s != null) {
            openDialog(s);
        } else {
            Others.showAlert(mainPane, "Vui lòng chọn sản phẩm cần sửa!", true);
        }
    }

    private void openDialog(Product p) {
        try {
            FXMLLoader l = new FXMLLoader(getClass().getResource("/GUI/Staff/ProductDialog.fxml"));
            AnchorPane pane = l.load();
            if (p != null) ((ProductDialogController)l.getController()).setProduct(p);
            Stage s = new Stage();
            s.setScene(new Scene(pane));
            s.showAndWait();
            loadProducts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowInactive() {
        showingInactive = chkShowInactive.isSelected();

        if (showingInactive) {
            btnDisable.setText("✅ Bán lại");
            btnDisable.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 15; -fx-cursor: hand;");
            updateStatus("Đang xem sản phẩm đã ngừng kinh doanh", 0);
        } else {
            btnDisable.setText("⛔ Ngừng KD");
            btnDisable.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 15; -fx-cursor: hand;");
            updateStatus("Đang xem sản phẩm đang kinh doanh", 0);
        }

        loadProducts();
    }

    @FXML
    private void handleDisable() {
        Product selected = tblProduct.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Others.showAlert(mainPane, showingInactive
                    ? "Vui lòng chọn sản phẩm cần bán lại!"
                    : "Vui lòng chọn sản phẩm cần ngừng kinh doanh!", true);
            return;
        }

        if (showingInactive) {
            boolean isConfirm = Others.showCustomConfirm(
                    "Xác nhận bán lại",
                    "Bạn có chắc muốn bán lại sản phẩm: " + selected.getProductName() + "?",
                    "Đồng ý", "Hủy bỏ"
            );

            if (isConfirm) {
                String result = ProductBusiness.restartBusiness(selected.getProductID());
                if (result.equals("success")) {
                    Others.showAlert(mainPane, "Bán lại thành công!", false);
                    loadProducts();
                } else {
                    Others.showAlert(mainPane, "Lỗi: " + result, true);
                }
            }
        } else {
            boolean isConfirm = Others.showCustomConfirm(
                    "Ngừng kinh doanh",
                    "Bạn có chắc muốn ngừng kinh doanh sản phẩm: " + selected.getProductName() + "?",
                    "Ngừng KD", "Hủy bỏ"
            );

            if (isConfirm) {
                String result = ProductBusiness.stopBusiness(selected.getProductID());
                if (result.equals("success")) {
                    Others.showAlert(mainPane, "Ngừng kinh doanh thành công!", false);
                    loadProducts();
                } else {
                    Others.showAlert(mainPane, "Lỗi: " + result, true);
                }
            }
        }
    }

    @FXML private void handleBack() {
        StaffFrameController.instance.switchForm("/GUI/Staff/ProductMenu.fxml");
    }

    private void updateStatus(String message, int count) {
        lblStatus.setText(message);
        lblCount.setText(count + " sản phẩm");
    }
}