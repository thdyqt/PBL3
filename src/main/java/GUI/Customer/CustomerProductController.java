package GUI.Customer;

import BusinessBLL.CategoryBusiness;
import BusinessBLL.ProductBusiness;
import EntityDTO.Category;
import EntityDTO.Product;
import Util.Others;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class CustomerProductController implements Initializable {

    // ==========================================
    // 1. KHAI BÁO CÁC THÀNH PHẦN GIAO DIỆN
    // ==========================================
    @FXML private VBox paneFilter;
    @FXML private Button btnToggleFilter;
    @FXML private Label lblResultCount;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbCategory;
    @FXML private TextField txtMinPrice;
    @FXML private TextField txtMaxPrice;
    @FXML private Button btnClearFilter;

    @FXML private ComboBox<String> cbSort;
    @FXML private ScrollPane scrollPane;
    @FXML private GridPane gridProducts;

    // ==========================================
    // 2. BIẾN DỮ LIỆU VÀ CACHE
    // ==========================================
    private List<Product> allProducts = new ArrayList<>();
    private Map<Integer, String> categoryCache = new HashMap<>();
    private boolean isFilterVisible = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupSortOptions();
        setupInputValidation();

        loadCategories();
        refreshData();

        setupListeners();
    }

    // ==========================================
    // 3. CẤU HÌNH HỆ THỐNG
    // ==========================================
    private void setupSortOptions() {
        cbSort.setItems(FXCollections.observableArrayList(
                "Mặc định",
                "Tên: A-Z",
                "Tên: Z-A",
                "Giá: Thấp đến Cao",
                "Giá: Cao đến Thấp",
                "Đánh giá: Cao nhất",
                "Tồn kho: Nhiều nhất"
        ));
        cbSort.getSelectionModel().selectFirst();
    }

    private void setupInputValidation() {
        txtMinPrice.textProperty().addListener((obs, old, nv) -> {
            if (!nv.matches("\\d*")) txtMinPrice.setText(nv.replaceAll("[^\\d]", ""));
        });
        txtMaxPrice.textProperty().addListener((obs, old, nv) -> {
            if (!nv.matches("\\d*")) txtMaxPrice.setText(nv.replaceAll("[^\\d]", ""));
        });
    }

    private void setupListeners() {
        txtSearch.textProperty().addListener((o, ov, nv) -> applyFilterAndSort());
        cbCategory.valueProperty().addListener((o, ov, nv) -> applyFilterAndSort());
        txtMinPrice.textProperty().addListener((o, ov, nv) -> applyFilterAndSort());
        txtMaxPrice.textProperty().addListener((o, ov, nv) -> applyFilterAndSort());
        cbSort.valueProperty().addListener((o, ov, nv) -> applyFilterAndSort());
    }

    // ==========================================
    // 4. XỬ LÝ DỮ LIỆU
    // ==========================================
    private void loadCategories() {
        cbCategory.getItems().setAll("Tất cả món");
        List<Category> list = CategoryBusiness.getAllCategories();
        for (Category c : list) {
            cbCategory.getItems().add(c.getCategoryName());
            categoryCache.put(c.getCategoryID(), c.getCategoryName());
        }
        cbCategory.getSelectionModel().selectFirst();
    }

    private void refreshData() {
        allProducts = ProductBusiness.getAllProducts();
        applyFilterAndSort();
    }

    private void applyFilterAndSort() {
        // BƯỚC 1: LỌC (FILTER)
        String keyword = txtSearch.getText().toLowerCase().trim();
        String selectedCate = cbCategory.getValue();

        long minPrice = txtMinPrice.getText().isEmpty() ? 0 : Long.parseLong(txtMinPrice.getText());
        long maxPrice = txtMaxPrice.getText().isEmpty() ? Long.MAX_VALUE : Long.parseLong(txtMaxPrice.getText());

        List<Product> result = allProducts.stream()
                .filter(p -> p.getProductName().toLowerCase().contains(keyword))
                .filter(p -> {
                    if (selectedCate == null || selectedCate.equals("Tất cả món")) return true;
                    return selectedCate.equals(categoryCache.get(p.getCategoryID()));
                })
                .filter(p -> p.getProductPrice() >= minPrice && p.getProductPrice() <= maxPrice)
                .collect(Collectors.toList());

        // BƯỚC 2: SẮP XẾP (SORT) - Đã tích hợp đẩy sản phẩm Hết hàng xuống cuối
        String sortType = cbSort.getValue();

        // Tiêu chí phụ: Theo lựa chọn của khách hàng
        Comparator<Product> secondarySort = Comparator.comparing(Product::getProductName); // Mặc định

        if (sortType != null) {
            switch (sortType) {
                case "Tên: A-Z" -> secondarySort = Comparator.comparing(Product::getProductName);
                case "Tên: Z-A" -> secondarySort = Comparator.comparing(Product::getProductName).reversed();
                case "Giá: Thấp đến Cao" -> secondarySort = Comparator.comparingInt(Product::getProductPrice);
                case "Giá: Cao đến Thấp" -> secondarySort = Comparator.comparingInt(Product::getProductPrice).reversed();
                case "Đánh giá: Cao nhất" -> secondarySort = Comparator.comparingDouble(Product::getRating).reversed();
                case "Tồn kho: Nhiều nhất" -> secondarySort = Comparator.comparingInt(Product::getQuantity).reversed();
            }
        }

        // Tiêu chí chính (Ưu tiên số 1): Còn hàng đưa lên đầu (isAvailable() = true sẽ được ưu tiên nhờ .reversed())
        Comparator<Product> primarySort = Comparator.comparing(Product::isAvailable).reversed();

        // Gộp 2 tiêu chí lại: Sắp xếp theo Tình trạng kho TRƯỚC, sau đó mới tới Tên/Giá
        result.sort(primarySort.thenComparing(secondarySort));

        // BƯỚC 3: HIỂN THỊ
        displayProducts(result);
        lblResultCount.setText("Hiển thị " + result.size() + " sản phẩm");
    }

    private void displayProducts(List<Product> products) {
        gridProducts.getChildren().clear();
        int column = 0;
        int row = 0;

        try {
            for (Product p : products) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Customer/CustomerProductCard.fxml"));
                VBox card = loader.load();

                CustomerProductCardController controller = loader.getController();
                controller.setData(p, new CustomerProductCardController.ProductCardListener() {
                    @Override
                    public void onViewDetails(Product product) {
                        handleViewDetails(product);
                    }

                    @Override
                    public void onAddToCart(Product product) {
                        handleAddToCart(product);
                    }
                });

                if (column == 3) {
                    column = 0;
                    row++;
                }

                gridProducts.add(card, column++, row);
                GridPane.setMargin(card, new Insets(10));

                FadeTransition ft = new FadeTransition(Duration.millis(500), card);
                ft.setFromValue(0); ft.setToValue(1); ft.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // 5. XỬ LÝ SỰ KIỆN
    // ==========================================
    @FXML
    private void handleToggleFilter(ActionEvent event) {
        isFilterVisible = !isFilterVisible;
        paneFilter.setVisible(isFilterVisible);
        paneFilter.setManaged(isFilterVisible);
        btnToggleFilter.setText(isFilterVisible ? "󰈺 Ẩn bộ lọc" : "󰈺 Hiện bộ lọc");
    }

    @FXML
    private void handleClearFilter(ActionEvent event) {
        txtSearch.clear();
        cbCategory.getSelectionModel().selectFirst();
        txtMinPrice.clear();
        txtMaxPrice.clear();
        cbSort.getSelectionModel().selectFirst();
        applyFilterAndSort();
    }

    private void handleViewDetails(Product product) {
        Others.showAlert(gridProducts, "Xem chi tiết: " + product.getProductName(), false);
    }

    private void handleAddToCart(Product product) {
        Others.showAlert(gridProducts, "Đã thêm " + product.getProductName() + " vào giỏ hàng!", false);
    }
}