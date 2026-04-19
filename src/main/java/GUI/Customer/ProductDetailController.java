package GUI.Customer;

import BusinessBLL.ReviewBusiness;
import EntityDTO.Product;
import EntityDTO.ProductReview;
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
import javafx.stage.Stage;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class ProductDetailController implements Initializable {
    @FXML
    private ImageView imgProduct;
    @FXML
    private Label lblProductName, lblPrice, lblStock, lblDescription, lblIngredients, lblAverageRating, lblReviewStatus;
    @FXML
    private Spinner<Integer> spinQuantity;
    @FXML
    private Button btnAddToCart, btnSubmitReview;
    @FXML
    private TextArea txtReviewComment;
    @FXML
    private HBox starRatingContainer;
    @FXML
    private VBox paneWriteReview, vboxOtherReviews;

    private Product currentProduct;
    private int selectedRating = 0;
    private Label[] starLabels = new Label[5];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        spinQuantity.setValueFactory(valueFactory);

        setupStarRatingSystem();

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
            lblStock.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-background-radius: 6; -fx-padding: 4 12;");
            btnAddToCart.setDisable(true);
        }

        checkUserReviewPermission();
        loadExistingReviews();
    }

    private void setupStarRatingSystem() {
        starRatingContainer.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            int ratingValue = i + 1;
            Label star = new Label("☆");
            star.setStyle("-fx-font-size: 30; -fx-cursor: hand;");
            star.setTextFill(Color.web("#D4891A"));

            star.setOnMouseClicked(e -> updateStarUI(ratingValue));

            starLabels[i] = star;
            starRatingContainer.getChildren().add(star);
        }
    }

    private void updateStarUI(int rating) {
        this.selectedRating = rating;
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                starLabels[i].setText("★");
            } else {
                starLabels[i].setText("☆");
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
            lblReviewStatus.setText("Bạn cần mua sản phẩm hoặc đã đánh giá rồi.");
            lblReviewStatus.setTextFill(Color.RED);
        } else {
            paneWriteReview.setOpacity(1.0);
            btnSubmitReview.setDisable(false);
            txtReviewComment.setDisable(false);
            starRatingContainer.setDisable(false);
            lblReviewStatus.setText("Cảm ơn bạn đã mua hàng! Hãy để lại đánh giá nhé.");
            lblReviewStatus.setTextFill(Color.web("#166534"));
        }
    }

    @FXML
    void handleSubmitReview(ActionEvent event) {
        if (selectedRating == 0) {
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
        newReview.setRating(selectedRating);
        newReview.setComment(comment);

        String result = ReviewBusiness.addReview(newReview);

        if ("success".equals(result)) {
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

            Label lblStars = new Label(getStarsString(review.getRating()));
            lblStars.setStyle("-fx-text-fill: #D4891A; -fx-font-size: 14;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS); // Đẩy ngày tháng sang sát lề phải

            Label lblDate = new Label(sdf.format(review.getReviewDate()));
            lblDate.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12;");

            headerBox.getChildren().addAll(lblName, lblStars, spacer, lblDate);

            Label lblComment = new Label(review.getComment());
            lblComment.setWrapText(true);
            lblComment.setStyle("-fx-text-fill: #475569; -fx-font-size: 14;");

            reviewBox.getChildren().addAll(headerBox, lblComment);
            vboxOtherReviews.getChildren().add(reviewBox);
        }
    }

    private String getStarsString(int rating) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < rating) sb.append("★");
            else sb.append("☆");
        }
        return sb.toString();
    }

    @FXML
    void handleAddToCart(ActionEvent event) {
        int quantity = spinQuantity.getValue();
        // CartManager.addToCart(currentProduct, quantity);

        Others.showAlert(lblProductName, "Đã thêm " + quantity + " " + currentProduct.getProductName() + " vào giỏ hàng!", false);
    }

    @FXML
    void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}