package GUI.Customer;

import BusinessBLL.CategoryBusiness;
import BusinessBLL.ProductBusiness;
import EntityDTO.Category;
import EntityDTO.Product;
import Util.CartManager;
import Util.Others;
import Util.UserSession;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import BusinessBLL.OrderBusiness;
import DataDAL.PromoCodeData;
import EntityDTO.Order;
import EntityDTO.PromoCode;
import javafx.scene.web.WebView;

import java.awt.Desktop;
import java.net.URI;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CustomerHomeController implements Initializable {
    @FXML private AnchorPane mainPane;
    @FXML private Label lblWelcome;
    @FXML private TextField txtSearch;
    @FXML private HBox hboxBannerIndicators;
    @FXML private HBox hboxCategories;
    @FXML private HBox hboxBestSellers;
    @FXML private HBox hboxNewArrivals;
    @FXML private StackPane paneBanner;
    @FXML private HBox   hboxPromoCodes;
    @FXML private VBox vboxRecentOrders;
    @FXML private Label  lblViewAllOrders;
    @FXML private WebView mapView;

    private List<String> bannerUrls = new ArrayList<>();
    private int currentBannerIndex = 0;
    private Timeline bannerTimeline;
    private FadeTransition bannerFadeTransition;
    private Region currentFadeOverlay;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupWelcomeMessage();
        setupBanner();
        setupCategories();
        setupSearchBox();

        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        clip.widthProperty().bind(paneBanner.widthProperty());
        clip.heightProperty().bind(paneBanner.heightProperty());
        paneBanner.setClip(clip);

        Platform.runLater(() -> {
            loadBestSellers();
            loadNewArrivals();
            loadPromoCodes();   // ← thêm
            loadRecentOrders(); // ← thêm
            loadMap(); // ← thêm
        });
    }

    private void setupWelcomeMessage() {
        if (UserSession.getInstance().isGuest()) {
            lblWelcome.setText("Xin chào, Khách!");
        } else {
            lblWelcome.setText("Xin chào, " + UserSession.getInstance().getName() + "!");
        }
    }

    private void setupBanner() {
        String[] imagePaths = {
                "/images/banner1.png",
                "/images/banner2.png",
        };

        for (String path : imagePaths) {
            try {
                URL url = getClass().getResource(path);
                if (url != null) {
                    bannerUrls.add(url.toExternalForm());
                } else {
                    System.out.println("Không tìm thấy ảnh: " + path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (bannerUrls.isEmpty()) return;

        hboxBannerIndicators.getChildren().clear();
        for (int i = 0; i < bannerUrls.size(); i++) {
            Region dot = new Region();
            dot.setCursor(javafx.scene.Cursor.HAND);

            final int index = i;
            dot.setOnMouseClicked(e -> {
                currentBannerIndex = index;
                updateBannerDisplay();

                if (bannerTimeline != null) {
                    bannerTimeline.playFromStart();
                }
            });
            hboxBannerIndicators.getChildren().add(dot);
        }

        updateBannerDisplay();

        bannerTimeline = new Timeline(new KeyFrame(Duration.seconds(7), event -> {
            currentBannerIndex = (currentBannerIndex + 1) % bannerUrls.size();
            updateBannerDisplay();
        }));
        bannerTimeline.setCycleCount(Timeline.INDEFINITE);
        bannerTimeline.play();
    }

    private void updateBannerDisplay() {
        if (bannerUrls.isEmpty()) return;

        String currentUrl = bannerUrls.get(currentBannerIndex);

        String imageStyle = "-fx-background-image: url('" + currentUrl + "'); " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center center; " +
                "-fx-background-radius: 20;";

        String fullStyle = imageStyle + " -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);";

        if (paneBanner.getStyle().isEmpty() || !paneBanner.getStyle().contains("-fx-background-image")) {
            paneBanner.setStyle(fullStyle);
            updateBannerIndicators();
            return;
        }

        if (bannerFadeTransition != null && bannerFadeTransition.getStatus() == Animation.Status.RUNNING) {
            bannerFadeTransition.stop();
            paneBanner.getChildren().remove(currentFadeOverlay);
            paneBanner.setStyle(fullStyle);
        }

        currentFadeOverlay = new Region();
        currentFadeOverlay.setStyle(imageStyle);
        currentFadeOverlay.setOpacity(0.0);

        paneBanner.getChildren().add(0, currentFadeOverlay);

        bannerFadeTransition = new FadeTransition(Duration.millis(800), currentFadeOverlay);
        bannerFadeTransition.setFromValue(0.0);
        bannerFadeTransition.setToValue(1.0);

        bannerFadeTransition.setOnFinished(e -> {
            paneBanner.setStyle(fullStyle);
            paneBanner.getChildren().remove(currentFadeOverlay);
        });

        bannerFadeTransition.play();
        updateBannerIndicators();
    }

    private void updateBannerIndicators() {
        for (int i = 0; i < hboxBannerIndicators.getChildren().size(); i++) {
            Region dot = (Region) hboxBannerIndicators.getChildren().get(i);
            if (i == currentBannerIndex) {
                dot.setStyle("-fx-background-color: #D4891A; -fx-background-radius: 10; -fx-opacity: 1.0;");
                dot.setPrefSize(25, 8);
            } else {
                dot.setStyle("-fx-background-color: white; -fx-background-radius: 50; -fx-opacity: 0.6;");
                dot.setPrefSize(8, 8);
            }
        }
    }

    @FXML
    private void handleViewMenu() {
        CustomerDashboardForm.instance.switchForm("/GUI/Customer/CustomerProduct.fxml");
        CustomerDashboardForm.instance.setActiveMenu(CustomerDashboardForm.instance.getBtnProducts());
    }

    private void switchToProductPage(String keyword, String categoryName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Customer/CustomerProduct.fxml"));
            Parent productView = loader.load();

            CustomerProductController controller = loader.getController();

            if (keyword != null && !keyword.isEmpty()) {
                controller.setSearchKeyword(keyword);
            }

            if (categoryName != null && !categoryName.isEmpty()) {
                controller.setSelectedCategory(categoryName);
            }

            mainPane.getChildren().clear();
            mainPane.getChildren().add(productView);

            AnchorPane.setTopAnchor(productView, 0.0);
            AnchorPane.setBottomAnchor(productView, 0.0);
            AnchorPane.setLeftAnchor(productView, 0.0);
            AnchorPane.setRightAnchor(productView, 0.0);

            if (CustomerDashboardForm.instance != null) {
                CustomerDashboardForm.instance.setActiveMenu(CustomerDashboardForm.instance.getBtnProducts());
            }

        } catch (Exception ex) {
            System.err.println("Lỗi khi chuyển sang trang Sản Phẩm: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void setupSearchBox() {
        txtSearch.setOnAction(e -> {
            String keyword = txtSearch.getText().trim();
            if (!keyword.isEmpty()) {
                switchToProductPage(keyword, null);
            }
        });
    }

    private void setupCategories() {
        hboxCategories.getChildren().clear();

        List<Category> categoryList = CategoryBusiness.getAllCategories();

        int limit = Math.min(categoryList.size(), 6);

        for (int i = 0; i < limit; i++) {
            Category cat = categoryList.get(i);
            String catName = cat.getCategoryName();

            Label lblCategory = new Label(catName);

            String defaultStyle = "-fx-background-color: white; -fx-padding: 10 25; " +
                    "-fx-background-radius: 20; -fx-border-color: #CBD5E1; -fx-border-radius: 20; " +
                    "-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1E293B; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2); " +
                    "-fx-cursor: hand;";

            String hoverStyle = "-fx-background-color: #D4891A; -fx-padding: 10 25; " +
                    "-fx-background-radius: 20; -fx-border-color: #D4891A; -fx-border-radius: 20; " +
                    "-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white; " +
                    "-fx-cursor: hand;";

            lblCategory.setStyle(defaultStyle);

            lblCategory.setOnMouseEntered(e -> lblCategory.setStyle(hoverStyle));
            lblCategory.setOnMouseExited(e -> lblCategory.setStyle(defaultStyle));

            lblCategory.setOnMouseClicked(e -> {
                switchToProductPage(null, catName);
            });

            hboxCategories.getChildren().add(lblCategory);
        }
    }

    private void loadBestSellers() {
        List<Product> list = ProductBusiness.getTopBestSellers(5);
        populateProductCards(list, hboxBestSellers);
    }

    private void loadNewArrivals() {
        List<Product> list = ProductBusiness.getNewestProducts(5);
        populateProductCards(list, hboxNewArrivals);
    }

    private void populateProductCards(List<Product> list, HBox container) {
        container.getChildren().clear();
        int limit = Math.min(list.size(), 6);

        for (int i = 0; i < limit; i++) {
            Product p = list.get(i);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Customer/CustomerProductCard.fxml"));
                Parent cardNode = loader.load();

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

                container.getChildren().add(cardNode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            Others.showAlert(mainPane, "Đã thêm " + product.getProductName() + " vào giỏ hàng!", false);
        } else {
            Others.showAlert(mainPane, "Sản phẩm này đã hết hàng trong kho!", true);
        }
    }
    // ===== MÃ KHUYẾN MÃI =====
    private void loadPromoCodes() {
        hboxPromoCodes.getChildren().clear();

        List<PromoCode> codes = PromoCodeData.getAllPromoCodes()
                .stream()
                .filter(c -> c.getStatus() == PromoCode.CodeStatus.Active
                        && java.time.LocalDateTime.now().isAfter(c.getValidFrom())
                        && java.time.LocalDateTime.now().isBefore(c.getValidTo()))
                .collect(java.util.stream.Collectors.toList());

        if (codes.isEmpty()) {
            Label lblEmpty = new Label("Hiện không có mã khuyến mãi nào!");
            lblEmpty.setStyle("-fx-text-fill: #9C8472; -fx-font-family: 'Serif'; -fx-font-style: italic;");
            hboxPromoCodes.getChildren().add(lblEmpty);
            return;
        }

        int limit = Math.min(codes.size(), 4);
        for (int i = 0; i < limit; i++) {
            PromoCode code = codes.get(i);

            VBox card = new VBox(6);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 14; -fx-border-color: #F2B950; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); -fx-min-width: 160;");

            // Mã code
            Label lblCode = new Label(code.getCode());
            lblCode.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D4891A; -fx-font-family: 'Serif';");

            // Giá trị giảm
            String discountText = code.getDiscountType() == PromoCode.CodeType.Percent
                    ? "Giảm " + code.getDiscountValue() + "%"
                    : "Giảm " + formatMoney(code.getDiscountValue()) + "đ";
            Label lblDiscount = new Label(discountText);
            lblDiscount.setStyle("-fx-font-size: 13px; -fx-text-fill: #92400E; -fx-font-family: 'Serif';");

            // Hạn sử dụng
            Label lblExpiry = new Label("HSD: " + code.getValidTo().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
            ));
            lblExpiry.setStyle("-fx-font-size: 11px; -fx-text-fill: #9C8472; -fx-font-family: 'Serif';");

            // Nút copy
            Label btnCopy = new Label("📋 Sao chép mã");
            btnCopy.setStyle("-fx-background-color: #FFF8EE; -fx-text-fill: #D4891A; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px; -fx-cursor: hand; -fx-border-color: #F2B950; -fx-border-width: 1; -fx-border-radius: 8;");
            btnCopy.setOnMouseClicked(e -> {
                javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
                javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                content.putString(code.getCode());
                clipboard.setContent(content);
                Others.showAlert(mainPane, "Đã sao chép mã: " + code.getCode(), false);
            });

            card.getChildren().addAll(lblCode, lblDiscount, lblExpiry, btnCopy);
            hboxPromoCodes.getChildren().add(card);
        }
    }

    // ===== ĐƠN HÀNG GẦN NHẤT =====
    private void loadRecentOrders() {
        vboxRecentOrders.getChildren().clear();

        if (UserSession.getInstance().isGuest()) {
            Label lblGuest = new Label("Đăng nhập để xem đơn hàng gần nhất của bạn!");
            lblGuest.setStyle("-fx-text-fill: #9C8472; -fx-font-family: 'Serif'; -fx-font-style: italic;");
            vboxRecentOrders.getChildren().add(lblGuest);
            return;
        }

        int customerID = UserSession.getInstance().getId();

        List<Order> recentOrders = OrderBusiness.getAllOrder()
                .stream()
                .filter(o -> o.getCustomer() != null && o.getCustomer().getId() == customerID)
                .sorted((a, b) -> b.getOrderTime().compareTo(a.getOrderTime()))
                .limit(2)
                .collect(java.util.stream.Collectors.toList());

        if (recentOrders.isEmpty()) {
            Label lblEmpty = new Label("Bạn chưa có đơn hàng nào!");
            lblEmpty.setStyle("-fx-text-fill: #9C8472; -fx-font-family: 'Serif'; -fx-font-style: italic;");
            vboxRecentOrders.getChildren().add(lblEmpty);
            return;
        }

        for (Order order : recentOrders) {
            HBox row = new HBox(12);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 14 18; -fx-border-color: #EEE5D8; -fx-border-width: 1; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 6, 0, 0, 1);");

            // Mã đơn
            Label lblID = new Label("#DH" + String.format("%03d", order.getId()));
            lblID.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3D2210; -fx-font-family: 'Serif';");

            // Ngày đặt
            Label lblDate = new Label(order.getOrderTime().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
            ));
            lblDate.setStyle("-fx-font-size: 12px; -fx-text-fill: #9C8472; -fx-font-family: 'Serif';");

            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            // Tổng tiền
            Label lblTotal = new Label(formatMoney(order.getFinalAmount()) + " đ");
            lblTotal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #D4891A; -fx-font-family: 'Serif';");

            // Trạng thái
            String statusText = switch (order.getStatus()) {
                case Waiting_for_validation -> "⏳ Chờ xác nhận";
                case Processing             -> "👨‍🍳 Đang chuẩn bị";
                case Delivering             -> "🚗 Đang giao";
                case Finished               -> "✅ Hoàn thành";
                case Cancelled              -> "❌ Đã hủy";
                default                     -> "Không rõ";
            };
            Label lblStatus = new Label(statusText);
            lblStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B4020; -fx-font-family: 'Serif';");

            row.getChildren().addAll(lblID, lblDate, spacer, lblTotal, lblStatus);
            vboxRecentOrders.getChildren().add(row);
        }
    }

    // ===== BẢN ĐỒ =====

    // ===== XEM TẤT CẢ ĐƠN HÀNG =====
    @FXML
    private void handleViewAllOrders() {
        CustomerDashboardForm.instance.switchForm("/GUI/Customer/MyOrderView.fxml");
    }

    // ===== MẠNG XÃ HỘI =====
    @FXML
    private void handleOpenFacebook() {
        try {
            Desktop.getDesktop().browse(new URI("https://www.facebook.com/share/1AGLKXaAby/"));
        } catch (Exception e) {
            System.err.println("Lỗi mở Facebook: " + e.getMessage());
        }
    }
    private void loadMap() {
        // ✅ Dùng OpenStreetMap — miễn phí, không cần API key, không bị chặn
        String mapHtml = """
        <html>
        <head>
            <meta charset="UTF-8">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body { margin: 0; padding: 0; }
                #map { width: 100%%; height: 220px; border-radius: 10px; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map').setView([16.0732, 108.1508], 17);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '© OpenStreetMap'
                }).addTo(map);
                L.marker([16.0732, 108.1508])
                    .addTo(map)
                    .bindPopup('<b>DUT Bakery</b><br>54 Nguyễn Lương Bằng, Đà Nẵng')
                    .openPopup();
            </script>
        </body>
        </html>
        """;
        mapView.getEngine().loadContent(mapHtml);

        // ✅ Cho phép JavaScript chạy trong WebView
        mapView.getEngine().setJavaScriptEnabled(true);
    }
    @FXML
    private void handleOpenInstagram() {
        try {
            Desktop.getDesktop().browse(new URI("https://instagram.com/dutbakery"));
        } catch (Exception e) {
            System.err.println("Lỗi mở Instagram: " + e.getMessage());
        }
    }

    // Thêm hàm formatMoney nếu chưa có
    private String formatMoney(int amount) {
        return NumberFormat.getNumberInstance(new Locale("vi", "VN")).format(amount);
    }
}