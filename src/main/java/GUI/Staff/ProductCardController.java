package GUI.Staff;

import EntityDTO.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.io.InputStream;

import static Util.Others.vn;

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

        lblProductId.setText("#" + product.getProductID());
        lblProductName.setText(product.getProductName());
        lblStock.setText(String.valueOf(product.getQuantity()));

        lblPrice.setText(String.format(vn, "%,.0f đ", (double) product.getProductPrice()));

        String imageName = product.getImage();

        if (imageName != null && !imageName.trim().isEmpty()) {

            String fullPath = "/images/" + imageName;

            if (imageName.startsWith("images/") || imageName.startsWith("/images/")) {
                fullPath = imageName.startsWith("/") ? imageName : "/" + imageName;
            }

            try {
                InputStream imageStream = getClass().getResourceAsStream(fullPath);
                if (imageStream != null) {
                    imgProduct.setImage(new Image(imageStream)); // Tải ảnh thành công!
                } else {
                    System.out.println("⚠️ CẢNH BÁO: Không tìm thấy file ảnh tại thư mục: " + fullPath);
                    imgProduct.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
                }
            } catch (Exception e) {
                imgProduct.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
            }
        } else {
            imgProduct.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
        }

        if (product.getQuantity() <= 0) {
            cardContainer.setOpacity(0.55);
            cardContainer.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 15; -fx-border-color: #CBD5E1; -fx-border-radius: 15; -fx-border-width: 1.5;");

            lblStock.setText("Hết hàng");
            lblStock.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;"); // Chữ đỏ báo động

            cardContainer.setCursor(javafx.scene.Cursor.DEFAULT);
            cardContainer.setOnMouseEntered(null);
            cardContainer.setOnMouseExited(null);

        } else {
            cardContainer.setOpacity(1.0);
            cardContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 15; -fx-border-width: 1.5;");

            lblStock.setText(String.valueOf(product.getQuantity()));
            lblStock.setStyle("-fx-text-fill: #2563EB; -fx-font-weight: bold;");

            cardContainer.setCursor(javafx.scene.Cursor.HAND);
            cardContainer.setOnMouseEntered(e -> {
                cardContainer.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 15; -fx-border-color: #2563EB; -fx-border-radius: 15; -fx-border-width: 2;");
            });
            cardContainer.setOnMouseExited(e -> {
                cardContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 15; -fx-border-width: 1.5;");
            });
        }
    }

    public Product getProduct() {
        return currentProduct;
    }
}

