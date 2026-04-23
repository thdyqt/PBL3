package GUI.Customer;

import BusinessBLL.ProductBusiness;
import BusinessBLL.ReviewBusiness;
import EntityDTO.Product;
import EntityDTO.ProductReview;
import Util.CartManager;
import Util.Others;
import Util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class ProductDetailController implements Initializable {
    @FXML private ImageView imgProduct;
    @FXML private Text lblProductName, lblPrice, lblStock, lblDescription, lblIngredients, lblAverageRating, lblReviewStatus;
    @FXML private HBox boxStock;
    @FXML private Spinner<Integer> spinQuantity;
    @FXML private Button btnAddToCart, btnSubmitReview;
    @FXML private TextArea txtReviewComment;
    @FXML private HBox starRatingContainer;
    @FXML private VBox paneWriteReview, vboxOtherReviews;

    private Product currentProduct;
    private int currentRating = 0;
    private Label[] starLabels = new Label[5];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        spinQuantity.setValueFactory(valueFactory);
        setupStarRating();
        Others.playButtonAnimation(btnAddToCart);
        Others.playButtonAnimation(btnSubmitReview);
    }

    public void setData(Product product) {
        this.currentProduct = product;

        lblProductName.setText(product.getProductName());
        lblPrice.setText(Others.formatPrice(product.getProductPrice()));
        lblDescription.setText(product.getDescription());
        lblIngredients.setText(product.getIngredients());
        lblAverageRating.setText(String.format("(%.1f ★)", product.getRating()));

        Others.loadImage(product.getImage(), imgProduct, 360, 360);

        if (product.getQuantity() <= 0) {
            lblStock.setText("Tạm hết hàng");
            lblStock.setFill(Color.web("#991B1B"));
            boxStock.setStyle("-fx-background-color: #FEE2E2; -fx-background-radius: 8; -fx-padding: 5 15;");
            btnAddToCart.setDisable(true);
        }

        else lblStock.setText("Còn " + currentProduct.getQuantity() + " sản phẩm");

        checkUserReviewPermission();
        loadExistingReviews();
    }

    private void setupStarRating() {
        starRatingContainer.getChildren().clear();

        for (int i = 1; i <= 5; i++) {
            Label star = new Label("★");
            star.setStyle("-fx-font-size: 30px; -fx-text-fill: #D4891A; -fx-cursor: hand;");

            final int ratingValue = i;

            star.setOnMouseEntered(e -> updateStars(ratingValue));
            star.setOnMouseExited(e -> updateStars(currentRating));

            star.setOnMouseClicked(e -> {
                currentRating = ratingValue;
                updateStars(currentRating);
            });

            starRatingContainer.getChildren().add(star);
        }
        updateStars(currentRating);
    }

    private void updateStars(int rating) {
        for (int i = 0; i < starRatingContainer.getChildren().size(); i++) {
            Label star = (Label) starRatingContainer.getChildren().get(i);
            if (i < rating) {
                star.setStyle("-fx-font-size: 30px; -fx-text-fill: #D4891A; -fx-cursor: hand;");
            } else {
                star.setStyle("-fx-font-size: 30px; -fx-text-fill: #CBD5E1; -fx-cursor: hand;");
            }
        }
    }

    private void checkUserReviewPermission() {
        if (UserSession.getInstance() == null) return;
        int currentUserId = UserSession.getInstance().getId();

        boolean canReview = ReviewBusiness.canUserReview(currentUserId, currentProduct.getProductID());

        if (!canReview) {
            paneWriteReview.setOpacity(0.6);
            btnSubmitReview.setDisable(true);
            txtReviewComment.setDisable(true);
            starRatingContainer.setDisable(true);
            lblReviewStatus.setText("Bạn cần mua sản phẩm hoặc bạn đã đánh giá rồi.");
            lblReviewStatus.setFill(Color.RED);
        } else {
            paneWriteReview.setOpacity(1.0);
            btnSubmitReview.setDisable(false);
            txtReviewComment.setDisable(false);
            starRatingContainer.setDisable(false);
            lblReviewStatus.setText("Cảm ơn bạn đã mua hàng! Hãy để lại đánh giá nhé.");
            lblReviewStatus.setFill(Color.web("#166534"));
        }
    }

    @FXML
    void handleSubmitReview(ActionEvent event) {
        if (currentRating == 0) {
            Others.showAlert(lblProductName, "Vui lòng chọn số sao đánh giá!", true);
            return;
        }

        String comment = txtReviewComment.getText().trim();
        if (comment.isEmpty()) {
            Others.showAlert(lblProductName, "Vui lòng viết vài lời cảm nhận nhé!", true);
            return;
        }

        int currentUserId = UserSession.getInstance().getId();
        ProductReview newReview = new ProductReview();
        newReview.setProductID(currentProduct.getProductID());
        newReview.setCustomerID(currentUserId);
        newReview.setRating(currentRating);
        newReview.setComment(comment);

        String result = ReviewBusiness.addReview(newReview);

        if ("success".equals(result)) {
            double newAverage = ProductBusiness.getProductRating(currentProduct.getProductID());
            lblAverageRating.setText(String.format("(%.1f ★)", newAverage));
            Others.showAlert(lblProductName, "Cảm ơn bạn đã gửi đánh giá!", false);
            checkUserReviewPermission();
            txtReviewComment.clear();
            loadExistingReviews();
        } else {
            Others.showAlert(lblProductName, result, true);
        }
    }

    private void loadExistingReviews() {
        vboxOtherReviews.getChildren().clear();
        List<ProductReview> list = ReviewBusiness.getReviewsOfProduct(currentProduct.getProductID());

        if (list == null || list.isEmpty()) {
            Label lblEmpty = new Label("Chưa có đánh giá nào. Hãy là người đầu tiên trải nghiệm!");
            lblEmpty.setStyle("-fx-text-fill: #94A3B8; -fx-font-style: italic; -fx-font-size: 14;");

            lblEmpty.setMinHeight(Region.USE_PREF_SIZE);
            vboxOtherReviews.getChildren().add(lblEmpty);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (ProductReview review : list) {
            VBox reviewBox = new VBox(8);
            reviewBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #F1F5F9; -fx-border-radius: 10;");

            HBox headerBox = new HBox(10);
            headerBox.setAlignment(Pos.CENTER_LEFT);

            Label lblName = new Label(review.getCustomerName());
            lblName.setStyle("-fx-font-weight: bold; -fx-text-fill: #0F172A; -fx-font-size: 14;");
            lblName.setMinHeight(Region.USE_PREF_SIZE);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) sb.append(i < review.getRating() ? "★" : "☆");
            Label lblStars = new Label(sb.toString());
            lblStars.setStyle("-fx-text-fill: #D4891A; -fx-font-size: 14;");
            lblStars.setMinHeight(Region.USE_PREF_SIZE);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            String dateStr = (review.getReviewDate() != null) ? sdf.format(review.getReviewDate()) : "Không rõ thời gian";
            Label lblDate = new Label(dateStr);
            lblDate.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12;");
            lblDate.setMinHeight(Region.USE_PREF_SIZE);

            headerBox.getChildren().addAll(lblName, lblStars, spacer, lblDate);

            Label lblComment = new Label(review.getComment());
            lblComment.setWrapText(true);
            lblComment.setStyle("-fx-text-fill: #475569; -fx-font-size: 14;");
            lblComment.setMinHeight(Region.USE_PREF_SIZE);

            reviewBox.getChildren().addAll(headerBox, lblComment);
            vboxOtherReviews.getChildren().add(reviewBox);
        }
    }

    @FXML
    void handleAddToCart(ActionEvent event) {
        int quantity = spinQuantity.getValue();
        int customerId = 0;
        if (!Util.UserSession.getInstance().isGuest()) {
            customerId = Util.UserSession.getInstance().getId();
        }

        boolean success = CartManager.getInstance().addToCustomerCart(customerId, currentProduct, quantity);

        if (success) {
            Others.showAlert(lblProductName, "Đã thêm " + quantity + " " + currentProduct.getProductName() + " vào giỏ hàng!", false);
        } else {
            Others.showAlert(lblProductName, "Rất tiếc, kho không đủ số lượng bạn yêu cầu!", true);
        }
    }

    @FXML
    void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}