package GUI.Staff;


import DataDAL.CategoryData;
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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CategoryController implements Initializable, IContentArea {
    private StackPane contentArea;



    // ===== FXML COMPONENTS =====
    @FXML private Button   btnBack;
    @FXML private Button   btnAddCategory;
    @FXML private Button   btnEditCategory;
    @FXML private Button   btnStopCategory;

    @FXML private TextField        txtSearchCategory;
    @FXML private ListView<Category> lvCategory;

    @FXML private Label   lblCategoryTitle;
    @FXML private Label   lblProductCount;
    @FXML private Label   lblStatus;
    @FXML private Label   lblCount;

    @FXML private TableView<Product>           tblProduct;
    @FXML private TableColumn<Product, Integer> colSTT;
    @FXML private TableColumn<Product, Integer> colProductID;
    @FXML private TableColumn<Product, String>  colProductName;
    @FXML private TableColumn<Product, Integer> colProductPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, Boolean> colIsAvailable;
    @FXML private TableColumn<Product, String>  colImage;

    // ===== DATA =====
    private ObservableList<Category> masterCategory = FXCollections.observableArrayList();
    private FilteredList<Category>   filteredCategory;
    private ObservableList<Product>  productList    = FXCollections.observableArrayList();

    // ===== INITIALIZE =====
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupCategoryList();
        loadCategories();
        setupSearch();
        setupCategoryClick();
    }

    // ===== SETUP LISTVIEW DANH MỤC =====
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
                    setStyle("-fx-font-size: 13px; -fx-text-fill: #F5DEB3;" +
                            "-fx-font-family: 'Serif'; -fx-padding: 10 15;" +
                            "-fx-background-color: transparent; -fx-cursor: hand;");
                }
            }
        });
    }

    // ===== LOAD DANH MỤC =====
    private void loadCategories() {
        List<Category> listFromDB = CategoryData.getAll();
        masterCategory.setAll(listFromDB);
        filteredCategory = new FilteredList<>(masterCategory, b -> true);
        SortedList<Category> sortedData = new SortedList<>(filteredCategory);
        lvCategory.setItems(sortedData);
        lblCount.setText(masterCategory.size() + " danh mục");
        lblStatus.setText("Tải dữ liệu thành công!");
    }

    // ===== TÌM KIẾM DANH MỤC =====
    private void setupSearch() {
        txtSearchCategory.textProperty().addListener((obs, oldVal, newVal) -> {
            String keyword = newVal.toLowerCase().trim();
            filteredCategory.setPredicate(category ->
                    category.getCategoryName().toLowerCase().contains(keyword)
            );
            lblCount.setText(filteredCategory.size() + " danh mục");
        });
    }

    // ===== CLICK VÀO DANH MỤC → HIỆN SẢN PHẨM =====
    private void setupCategoryClick() {
        // Thay listener bằng mouse click trực tiếp
        lvCategory.setOnMouseClicked(event -> {
            Category selected = lvCategory.getSelectionModel().getSelectedItem();
            System.out.println("Mouse clicked, selected: " +
                    (selected == null ? "null" : selected.getCategoryName()));
            if (selected != null) {
                loadProductsByCategory(selected);
            }
        });
    }

    private void loadProductsByCategory(Category category) {
        System.out.println("=== CLICK DANH MỤC ===");
        System.out.println("Category ID: " + category.getCategoryID());
        System.out.println("Category Name: " + category.getCategoryName());
        List<Product> products = ProductData.getByCategory(category.getCategoryID());
        productList.setAll(products);

        SortedList<Product> sortedProducts = new SortedList<>(productList);
        sortedProducts.comparatorProperty().bind(tblProduct.comparatorProperty());
        tblProduct.setItems(sortedProducts);
        Others.animateTableRows(tblProduct);

        lblCategoryTitle.setText("📂  " + category.getCategoryName());
        lblProductCount.setText(products.size() + " sản phẩm");
        lblStatus.setText("Đang xem: " + category.getCategoryName());
    }

    // ===== SETUP TABLE COLUMNS =====
    private void setupTableColumns() {
        colSTT.setCellValueFactory(col ->
                new javafx.beans.property.SimpleIntegerProperty(
                        tblProduct.getItems().indexOf(col.getValue()) + 1
                ).asObject()
        );

        colProductID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colProductPrice.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Trạng thái
        colIsAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));
        colIsAvailable.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item ? "✅ Còn hàng" : "❌ Hết hàng");
            }
        });

        // Hình ảnh
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

    // ===== XỬ LÝ THÊM DANH MỤC =====
    // ===== MỞ DIALOG =====
    private void openDialog(Category category) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/GUI/Staff/CategoryDialog.fxml")
            );
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

            loadCategories(); // Reload lại ListView sau khi đóng dialog

        } catch (Exception e) {
            System.err.println("Lỗi mở dialog: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddCategory() {
        openDialog(null);      // null = thêm mới
    }

    @FXML
    private void handleEditCategory() {
        Category selected = lvCategory.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("⚠️ Vui lòng chọn danh mục cần sửa!");
            return;
        }
        openDialog(selected);  // có data = sửa
    }
    // ===== XỬ LÝ NGỪNG KINH DOANH =====
    @FXML
    private void handleStopCategory() {
        Category selected = lvCategory.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("⚠️ Vui lòng chọn danh mục cần ngừng kinh doanh!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Ngừng kinh doanh danh mục?");
        confirm.setContentText("Bạn có chắc muốn ngừng: " + selected.getCategoryName() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean result = CategoryData.stopBusiness(selected.getCategoryID());
                if (result) {
                    showAlert("✅ Ngừng kinh doanh thành công!");
                    loadCategories();
                    tblProduct.getItems().clear();
                    lblCategoryTitle.setText("Chọn danh mục để xem sản phẩm");
                    lblProductCount.setText("");
                } else {
                    showAlert("❌ " + result);
                }
            }
        });
    }




    // ===== CONTENT AREA =====
    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }
    // ===== XỬ LÝ QUAY LẠI =====
    @FXML
    private void handleBack() {
       switchForm("productMenu.fxml");
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
    // ===== HELPER =====
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}