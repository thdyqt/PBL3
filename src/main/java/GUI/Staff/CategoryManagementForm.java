package GUI.Staff;

import BusinessBLL.CategoryBusiness;
import BusinessBLL.ProductBusiness;
import DataDAL.CategoryData;
import EntityDTO.Category;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CategoryManagementForm implements Initializable {
    @FXML private BorderPane mainPane;
    @FXML private Button btnBack, btnAddCategory, btnEditCategory, btnStopCategory;
    @FXML private TextField txtSearchCategory;
    @FXML private ListView<Category> lvCategory;
    @FXML private Label lblCategoryTitle, lblProductCount, lblStatus, lblCount;
    @FXML private CheckBox chkShowInactive;

    @FXML private TableView<Product> tblProduct;
    @FXML private TableColumn<Product, Integer> colSTT, colProductID, colProductPrice, colQuantity;
    @FXML private TableColumn<Product, String> colProductName, colImage;
    @FXML private TableColumn<Product, Boolean> colIsAvailable;

    // ===== DATA =====
    private ObservableList<Category> masterCategory = FXCollections.observableArrayList();
    private FilteredList<Category> filteredCategory;
    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private boolean showingInactive = false;

    // ===== INITIALIZE =====
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupCategoryList();
        loadCategories();
        setupSearch();
        setupCategoryClick();

        if (btnAddCategory != null) Others.playButtonAnimation(btnAddCategory);
        if (btnEditCategory != null) Others.playButtonAnimation(btnEditCategory);
        if (btnStopCategory != null) Others.playButtonAnimation(btnStopCategory);
        if (btnBack != null) Others.playButtonAnimation(btnBack);
    }

    private void setupCategoryList() {
        lvCategory.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("📁  " + item.getCategoryName());
                    setStyle("-fx-font-size: 14px; -fx-text-fill: #1E293B; -fx-font-weight: bold; -fx-padding: 10 15; -fx-background-color: transparent; -fx-cursor: hand;");
                }
            }
        });
    }

    private void loadCategories() {
        List<Category> listFromDB = showingInactive
                ? CategoryData.getInactiveCategories()
                : CategoryData.getAll();

        masterCategory.setAll(listFromDB);
        filteredCategory = new FilteredList<>(masterCategory, b -> true);
        SortedList<Category> sortedData = new SortedList<>(filteredCategory);
        lvCategory.setItems(sortedData);
        lblCount.setText(masterCategory.size() + " danh mục");
    }

    @FXML
    private void handleShowInactive() {
        showingInactive = chkShowInactive.isSelected();

        if (showingInactive) {
            btnStopCategory.setText("✅ Bán lại");
            btnStopCategory.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10; -fx-cursor: hand;");
        } else {
            btnStopCategory.setText("⛔ Ngừng");
            btnStopCategory.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10; -fx-cursor: hand;");
        }

        loadCategories();
    }

    private void setupSearch() {
        txtSearchCategory.textProperty().addListener((obs, oldVal, newVal) -> {
            String keyword = newVal.toLowerCase().trim();
            filteredCategory.setPredicate(category -> {
                if (keyword.isEmpty()) return true;

                String cateName = category.getCategoryName().toLowerCase();
                String cateID = String.valueOf(category.getCategoryID());

                return cateName.contains(keyword) || cateID.contains(keyword);
            });
            lblCount.setText(filteredCategory.size() + " danh mục");
        });
    }

    private void setupCategoryClick() {
        lvCategory.setOnMouseClicked(event -> {
            Category selected = lvCategory.getSelectionModel().getSelectedItem();
            if (selected != null) {
                loadProductsByCategory(selected);
            }
        });
    }

    private void loadProductsByCategory(Category category) {
        List<Product> products = ProductBusiness.getAllProductsByCategory(category.getCategoryID());
        productList.setAll(products);

        SortedList<Product> sortedProducts = new SortedList<>(productList);
        sortedProducts.comparatorProperty().bind(tblProduct.comparatorProperty());
        tblProduct.setItems(sortedProducts);
        Others.animateTableRows(tblProduct);

        lblCategoryTitle.setText("📂  " + category.getCategoryName());
        lblProductCount.setText(products.size() + " sản phẩm");
        lblStatus.setText("Đang xem: " + category.getCategoryName());
    }

    private void setupTableColumns() {
        colSTT.setCellValueFactory(col ->
                new javafx.beans.property.SimpleIntegerProperty(
                        tblProduct.getItems().indexOf(col.getValue()) + 1
                ).asObject()
        );

        colProductID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        colProductPrice.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        colProductPrice.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) setText(null);
                else setText(Others.formatPrice(price));
            }
        });

        colIsAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));
        colIsAvailable.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
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
                if (empty) { setGraphic(null); return; }
                try {
                    var stream = item != null
                            ? getClass().getResourceAsStream("/images/" + item)
                            : getClass().getResourceAsStream("/images/default.png");

                    if (stream != null) {
                        imageView.setImage(new Image(stream));
                        setGraphic(imageView);
                    } else {
                        setText("No image");
                        setGraphic(null);
                    }
                } catch (Exception e) {
                    setGraphic(null);
                }
            }
        });
    }

    private void openDialog(Category category) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Staff/CategoryDialog.fxml"));
            AnchorPane pane = loader.load();

            if (category != null) {
                CategoryDialogController controller = loader.getController();
                controller.setCategory(category);
            }

            Stage stage = new Stage();
            stage.setTitle(category == null ? "Thêm danh mục" : "Sửa danh mục");
            stage.setScene(new Scene(pane));
            stage.setResizable(false);
            stage.showAndWait();

            loadCategories();

        } catch (Exception e) {
            System.err.println("Lỗi mở dialog: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddCategory() {
        openDialog(null);
    }

    @FXML
    private void handleEditCategory() {
        Category selected = lvCategory.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Others.showAlert(mainPane, "Vui lòng chọn danh mục cần sửa!", true);
            return;
        }
        openDialog(selected);
    }

    @FXML
    private void handleStopCategory() {
        Category selected = lvCategory.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Others.showAlert(mainPane, showingInactive ? "Vui lòng chọn danh mục cần bán lại!" : "Vui lòng chọn danh mục muốn ngừng kinh doanh!", true);
            return;
        }

        if (showingInactive) {
            boolean isConfirm = Others.showCustomConfirm(
                    "Xác nhận bán lại",
                    "Bạn có chắc muốn kinh doanh lại danh mục: " + selected.getCategoryName() + "?",
                    "Đồng ý", "Hủy bỏ"
            );

            if (isConfirm) {
                String result = CategoryBusiness.restartBusiness(selected.getCategoryID());
                if (result.equals("success")) {
                    Others.showAlert(mainPane, "Kinh doanh lại thành công!", false);
                    productList.clear();
                    loadCategories();
                    lblCategoryTitle.setText("Chọn danh mục để xem sản phẩm");
                    lblProductCount.setText("0 sản phẩm");
                } else {
                    Others.showAlert(mainPane, "Lỗi: " + result, true);
                }
            }
        } else {
            boolean isConfirm = Others.showCustomConfirm(
                    "Ngừng kinh doanh",
                    "Bạn có chắc muốn ngừng kinh doanh danh mục: " + selected.getCategoryName() + "?",
                    "Ngừng KD", "Hủy bỏ"
            );

            if (isConfirm) {
                String result = CategoryBusiness.stopBusiness(selected.getCategoryID());
                if (result.equals("success")) {
                    Others.showAlert(mainPane, "Ngừng kinh doanh thành công!", false);
                    productList.clear();
                    loadCategories();
                    lblCategoryTitle.setText("Chọn danh mục để xem sản phẩm");
                    lblProductCount.setText("0 sản phẩm");
                } else {
                    Others.showAlert(mainPane, "Lỗi: " + result, true);
                }
            }
        }
    }

    @FXML
    private void handleBack() {
        StaffFrameController.instance.switchForm("/GUI/Staff/ProductMenu.fxml");
    }
}