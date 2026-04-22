package GUI.Customer;

import EntityDTO.Product;
import Util.Others;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

    public class CustomerProductCardController {
        @FXML private VBox cardBox;
        @FXML private ImageView imgProduct;
        @FXML private Label lblName;
        @FXML private Label lblPrice;
        @FXML private Label lblStatus;
        @FXML private Button btnDetails;
        @FXML private Button btnAddToCart;

        private Product currentProduct;
        private ProductCardListener listener;

        public interface ProductCardListener {
            void onViewDetails(Product product);
            void onAddToCart(Product product);
        }

        public void setData(Product product, ProductCardListener listener) {
            this.currentProduct = product;
            this.listener = listener;

            lblName.setText(product.getProductName());
            lblPrice.setText(Others.formatPrice(product.getProductPrice()));

            imgProduct.setEffect(null);

            if (product.getQuantity() <= 0) {
                lblStatus.setText("Hết hàng");
                lblStatus.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-background-color: transparent;");

                btnAddToCart.setDisable(true);
                cardBox.setOpacity(0.75);
                cardBox.setStyle("-fx-background-color: #FEF2F2; -fx-background-radius: 15; -fx-border-color: #FECACA; -fx-border-radius: 15; -fx-border-width: 1.5;");

                cardBox.setOnMouseEntered(e -> cardBox.setStyle("-fx-background-color: #FEE2E2; -fx-background-radius: 15; -fx-border-color: #EF4444; -fx-border-radius: 15; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(239,68,68,0.2), 10, 0, 0, 4);"));
                cardBox.setOnMouseExited(e -> cardBox.setStyle("-fx-background-color: #FEF2F2; -fx-background-radius: 15; -fx-border-color: #FECACA; -fx-border-radius: 15; -fx-border-width: 1.5;"));

            } else {
                lblStatus.setText("Số lượng: " + product.getQuantity());
                lblStatus.setStyle("-fx-text-fill: #166534; -fx-font-weight: bold; -fx-background-color: #DCFCE7; -fx-background-radius: 5; -fx-padding: 2 6;");

                btnAddToCart.setDisable(false);
                cardBox.setOpacity(1.0);
                cardBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 15; -fx-border-width: 1.5;");

                setupHoverEffect();
            }

            Others.loadImage(product.getImage(), imgProduct, 200, 200);

            Others.playButtonAnimation(btnDetails);
            Others.playButtonAnimation(btnAddToCart);
        }

    private void setupHoverEffect() {
        cardBox.setOnMouseEntered(e -> cardBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #D4891A; -fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(212,137,26,0.3), 15, 0, 0, 5);"));
        cardBox.setOnMouseExited(e -> cardBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);"));
    }

    @FXML private void handleViewDetails() { if (listener != null) listener.onViewDetails(currentProduct); }
    @FXML private void handleAddToCart() { if (listener != null) listener.onAddToCart(currentProduct); }
}