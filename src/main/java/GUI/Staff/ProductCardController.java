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

        lblProductId.setText("#" + product.getProductID());
        lblProductName.setText(product.getProductName());
        lblStock.setText(String.valueOf(product.getQuantity()));

        lblPrice.setText(String.format("%,.0f đ", (double) product.getProductPrice()));

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
    }

    public Product getProduct() {
        return currentProduct;
    }
}

