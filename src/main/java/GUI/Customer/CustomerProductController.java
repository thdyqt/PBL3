package GUI.Customer;

import BusinessBLL.CategoryBusiness;
import BusinessBLL.ProductBusiness;
import EntityDTO.Category;
import EntityDTO.Product;
import Util.CartManager;
import Util.Others;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class CustomerProductController implements Initializable {
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
    @FXML private FlowPane flowProducts;

    private List<Product> allProducts = new ArrayList<>();
    private Map<Integer, String> categoryCache = new HashMap<>();
    private boolean isFilterVisible = true;
    private double originalFilterWidth = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupSortOptions();
        setupInputValidation();
        flowProducts.prefWrapLengthProperty().bind(scrollPane.widthProperty().subtract(40));

        loadCategories();
        refreshData();
        setupListeners();
    }

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
        Others.setMaxLength(txtMinPrice, 8);
        Others.setMaxLength(txtMaxPrice, 8);

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
        // LỌC (FILTER)
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

        // SẮP XẾP (SORT)
        String sortType = cbSort.getValue();

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

        Comparator<Product> primarySort = Comparator.comparing(Product::isAvailable).reversed();

        result.sort(primarySort.thenComparing(secondarySort));

        displayProducts(result);
        lblResultCount.setText("Hiển thị " + result.size() + " sản phẩm");
    }

    private void displayProducts(List<Product> products) {
        flowProducts.getChildren().clear();

        for (Product p : products) {
            try {
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

                flowProducts.getChildren().add(card);

                FadeTransition ft = new FadeTransition(Duration.millis(500), card);
                ft.setFromValue(0); ft.setToValue(1); ft.play();

            } catch (Exception e) {
                System.err.println("❌ Không thể tải Card cho bánh: " + p.getProductName());
                e.printStackTrace();
            }
        }
    }

    public void setSearchKeyword(String keyword) {
        txtSearch.setText(keyword);
    }

    public void setSelectedCategory(String categoryName) {
        cbCategory.getSelectionModel().select(categoryName);
    }

    @FXML
    private void handleToggleFilter(ActionEvent event) {
        if (originalFilterWidth == 0) {
            originalFilterWidth = paneFilter.getWidth();
            paneFilter.setMinWidth(0);
        }

        isFilterVisible = !isFilterVisible;
        btnToggleFilter.setText(isFilterVisible ? "Ẩn bộ lọc" : "Hiện bộ lọc");

        Timeline timeline = new Timeline();

        if (!isFilterVisible) {
            KeyValue kvWidth = new KeyValue(paneFilter.prefWidthProperty(), 0, Interpolator.EASE_BOTH);
            KeyValue kvOpacity = new KeyValue(paneFilter.opacityProperty(), 0, Interpolator.EASE_BOTH);
            KeyFrame kf = new KeyFrame(Duration.millis(300), kvWidth, kvOpacity);

            timeline.getKeyFrames().add(kf);

            timeline.setOnFinished(e -> {
                paneFilter.setVisible(false);
                paneFilter.setManaged(false);
            });

        } else {
            paneFilter.setVisible(true);
            paneFilter.setManaged(true);

            KeyValue kvWidth = new KeyValue(paneFilter.prefWidthProperty(), originalFilterWidth, Interpolator.EASE_BOTH);
            KeyValue kvOpacity = new KeyValue(paneFilter.opacityProperty(), 1, Interpolator.EASE_BOTH);
            KeyFrame kf = new KeyFrame(Duration.millis(300), kvWidth, kvOpacity);

            timeline.getKeyFrames().add(kf);
        }

        timeline.play();
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Customer/ProductDetail.fxml"));
            Parent root = loader.load();

            ProductDetailController controller = loader.getController();
            controller.setData(product);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Chi tiết: " + product.getProductName());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAddToCart(Product product) {
        int customerId = 0;
        if (!Util.UserSession.getInstance().isGuest()) {
            customerId = Util.UserSession.getInstance().getId();
        }

        boolean success = CartManager.getInstance().addToCustomerCart(customerId, product, 1);

        if (success) {
            Others.showAlert(flowProducts, "Đã thêm " + product.getProductName() + " vào giỏ hàng!", false);
        } else {
            Others.showAlert(flowProducts, "Sản phẩm này đã hết hàng trong kho!", true);
        }
    }
}