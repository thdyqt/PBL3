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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
}