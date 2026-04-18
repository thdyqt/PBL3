package GUI.Customer;

import EntityDTO.Product;
import Util.Others;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

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

    private static final Map<String, Image> imageCache = new HashMap<>();

    public interface ProductCardListener {
        void onViewDetails(Product product);
        void onAddToCart(Product product);
    }

    public void setData(Product product, ProductCardListener listener) {
        // 🛡️ CHỐNG LỖI NGẦM MODULE-INFO:
        if (lblName == null) {
            System.err.println("❌ LỖI NGHIÊM TRỌNG: JavaFX không thể Inject các biến @FXML. " +
                    "Hãy kiểm tra lại file module-info.java xem đã có 'opens GUI.Customer to javafx.fxml;' chưa!");
            return;
        }

        this.currentProduct = product;
        this.listener = listener;

        DecimalFormat formatter = new DecimalFormat("###,###,###");
        lblName.setText(product.getProductName());
        lblPrice.setText(formatter.format(product.getProductPrice()) + " đ");

        if (product.getQuantity() <= 0) {
            lblStatus.setText("Hết hàng");
            lblStatus.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-background-radius: 5; -fx-padding: 2 6;");
            btnAddToCart.setDisable(true);
        } else {
            lblStatus.setText("Còn hàng");
            lblStatus.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #166534; -fx-background-radius: 5; -fx-padding: 2 6;");
            btnAddToCart.setDisable(false);
        }

        loadProductImageFast(product.getImage());

        Others.playButtonAnimation(btnDetails);
        Others.playButtonAnimation(btnAddToCart);
        setupHoverEffect();
    }

    private void loadProductImageFast(String imageName) {
        try {
            if (imageName == null || imageName.isEmpty()) imageName = "default.png";

            if (imageCache.containsKey(imageName)) {
                imgProduct.setImage(imageCache.get(imageName));
                return;
            }

            URL imageUrl = getClass().getResource("/images/" + imageName);
            if (imageUrl != null) {
                Image image = new Image(imageUrl.toExternalForm(), 200, 200, true, true, true);
                imageCache.put(imageName, image);
                imgProduct.setImage(image);
            }
        } catch (Exception e) {
            System.err.println("Lỗi load ảnh: " + imageName);
        }
    }

    private void setupHoverEffect() {
        cardBox.setOnMouseEntered(e -> cardBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #D4891A; -fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(212,137,26,0.3), 15, 0, 0, 5);"));
        cardBox.setOnMouseExited(e -> cardBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);"));
    }

    @FXML
    private void handleViewDetails() {
        if (listener != null) listener.onViewDetails(currentProduct);
    }

    @FXML
    private void handleAddToCart() {
        if (listener != null) listener.onAddToCart(currentProduct);
    }
}