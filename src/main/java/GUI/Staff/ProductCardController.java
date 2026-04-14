package GUI.Staff;

import EntityDTO.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.io.InputStream;

public class ProductCardController {
    @FXML
    private VBox cardContainer;

    @FXML
    private ImageView imgProduct;

    @FXML
    private Label lblPrice;

    @FXML
    private Label lblProductId;

    @FXML
    private Label lblProductName;

    @FXML
    private Label lblStock;

    private Product currentProduct;

    public void setData(Product product) {
        this.currentProduct = product;

        // Đổ text lên giao diện
        lblProductId.setText("#" + product.getProductID());
        lblProductName.setText(product.getProductName());
        lblStock.setText(String.valueOf(product.getQuantity()));
        lblPrice.setText(String.format("%,.0fđ", (double) product.getProductPrice()));

        // Xử lý nạp hình ảnh
        String imagePath = product.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                InputStream imageStream = getClass().getResourceAsStream("/" + imagePath);
                if (imageStream != null) {
                    imgProduct.setImage(new Image(imageStream));
                } else {
                    imgProduct.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
                }
            } catch (Exception e) {
                System.out.println("Lỗi tải ảnh cho SP: " + product.getProductName());
            }
        }

        cardContainer.setOnMouseEntered(e -> {
            cardContainer.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 15; -fx-border-color: #2563EB; -fx-border-radius: 15; -fx-border-width: 2;");
        });
        cardContainer.setOnMouseExited(e -> {
            cardContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 15; -fx-border-width: 1.5;");
        });
    }

    public Product getProduct() {
        return currentProduct;
    }
}

