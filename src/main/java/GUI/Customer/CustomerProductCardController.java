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

        if (product.getQuantity() <= 0) {
            lblStatus.setText("Hết hàng");
            lblStatus.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-background-radius: 5; -fx-padding: 2 6;");
            btnAddToCart.setDisable(true);
        } else {
            lblStatus.setText("Còn hàng");
            lblStatus.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #166534; -fx-background-radius: 5; -fx-padding: 2 6;");
            btnAddToCart.setDisable(false);
        }

        Others.loadImage(product.getImage(), imgProduct, 200, 200);

        Others.playButtonAnimation(btnDetails);
        Others.playButtonAnimation(btnAddToCart);
        setupHoverEffect();
    }

    private void setupHoverEffect() {
        cardBox.setOnMouseEntered(e -> cardBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #D4891A; -fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(212,137,26,0.3), 15, 0, 0, 5);"));
        cardBox.setOnMouseExited(e -> cardBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);"));
    }

    @FXML private void handleViewDetails() { if (listener != null) listener.onViewDetails(currentProduct); }
    @FXML private void handleAddToCart() { if (listener != null) listener.onAddToCart(currentProduct); }
}