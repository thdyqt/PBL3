package GUI.Staff;

import EntityDTO.Product;
import Util.Others;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class ProductCardController {
    @FXML private VBox cardContainer;
    @FXML private ImageView imgProduct;
    @FXML private Label lblPrice;
    @FXML private Label lblProductId;
    @FXML private Label lblProductName;
    @FXML private Label lblStock;

    private Product currentProduct;

    public void setData(Product product) {
        this.currentProduct = product;

        lblProductId.setText("#" + product.getProductID());
        lblProductName.setText(product.getProductName());
        lblPrice.setText(Others.formatPrice(product.getProductPrice()));

        if (product.getQuantity() <= 0) {
            lblStock.setText("Hết hàng");
            lblStock.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");

            imgProduct.setEffect(null);

            cardContainer.setDisable(false);
            cardContainer.setCursor(javafx.scene.Cursor.HAND);

            cardContainer.setOpacity(0.75);
            cardContainer.setStyle("-fx-background-color: #FEF2F2; -fx-background-radius: 15; -fx-border-color: #FECACA; -fx-border-radius: 15; -fx-border-width: 1.5;");

            cardContainer.setOnMouseEntered(e -> cardContainer.setStyle("-fx-background-color: #FEE2E2; -fx-background-radius: 15; -fx-border-color: #EF4444; -fx-border-radius: 15; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(239,68,68,0.2), 10, 0, 0, 4);"));
            cardContainer.setOnMouseExited(e -> cardContainer.setStyle("-fx-background-color: #FEF2F2; -fx-background-radius: 15; -fx-border-color: #FECACA; -fx-border-radius: 15; -fx-border-width: 1.5;"));

        } else {
            lblStock.setText(String.valueOf(product.getQuantity()));
            lblStock.setStyle("-fx-text-fill: #2563EB; -fx-font-weight: bold;");

            imgProduct.setEffect(null);
            cardContainer.setDisable(false);
            cardContainer.setCursor(javafx.scene.Cursor.HAND);

            cardContainer.setOpacity(1.0);
            cardContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 15; -fx-border-width: 1.5;");

            setupHoverEffect();
        }

        Others.loadImage(product.getImage(), imgProduct, 120, 120);
    }

    private void setupHoverEffect() {
        cardContainer.setOnMouseEntered(e -> cardContainer.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 15; -fx-border-color: #2563EB; -fx-border-radius: 15; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(37,99,235,0.2), 10, 0, 0, 4);"));
        cardContainer.setOnMouseExited(e -> cardContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 15; -fx-border-width: 1.5;"));
    }
}