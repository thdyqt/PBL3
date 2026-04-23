package GUI.Customer;

import EntityDTO.OrderDetail;
import Util.CartManager;
import Util.Others;
import Util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class CartItemCardController {
    @FXML private ImageView imgProduct;
    @FXML private Label lblName;
    @FXML private Label lblPrice;
    @FXML private TextField txtQuantity;

    private OrderDetail currentItem;
    private Runnable onUpdateListener;

    public void setData(OrderDetail item, Runnable onUpdateListener) {
        this.currentItem = item;
        this.onUpdateListener = onUpdateListener;

        lblName.setText(item.getProduct().getProductName());
        lblPrice.setText(Others.formatPrice(item.getPrice()));

        Others.setMaxLength(txtQuantity, 3);

        txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtQuantity.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        txtQuantity.setOnAction(e -> handleManualQuantityInput());

        txtQuantity.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Khi mất focus
                handleManualQuantityInput();
            }
        });

        Others.loadImage(item.getProduct().getImage(), imgProduct, 80, 80);
    }

    @FXML private void handlePlus() {
        int customerId = UserSession.getInstance().isGuest() ? 0 : UserSession.getInstance().getId();
        boolean success = CartManager.getInstance().addToCustomerCart(customerId, currentItem.getProduct(), 1);
        if (success) {
            txtQuantity.setText(String.valueOf(currentItem.getQuantity()));
            if (onUpdateListener != null) onUpdateListener.run();
        } else {
            Others.showAlert(lblName, "Sản phẩm đã hết hàng trong kho!", true);
        }
    }

    @FXML private void handleMinus() {
        if (currentItem.getQuantity() > 1) {
            int customerId = UserSession.getInstance().isGuest() ? 0 : UserSession.getInstance().getId();
            CartManager.getInstance().addToCustomerCart(customerId, currentItem.getProduct(), -1);
            txtQuantity.setText(String.valueOf(currentItem.getQuantity()));
            if (onUpdateListener != null) onUpdateListener.run();
        } else {
            handleDelete();
        }
    }

    private void handleManualQuantityInput() {
        if (txtQuantity.getText().isEmpty()) {
            txtQuantity.setText(String.valueOf(currentItem.getQuantity()));
            return;
        }

        int newQty = Integer.parseInt(txtQuantity.getText());

        if (newQty <= 0) {
            handleDelete();
        } else {
            int diff = newQty - currentItem.getQuantity();
            if (diff == 0) return;

            int customerId = UserSession.getInstance().isGuest() ? 0 : UserSession.getInstance().getId();
            boolean success = CartManager.getInstance().addToCustomerCart(customerId, currentItem.getProduct(), diff);

            if (success) {
                txtQuantity.setText(String.valueOf(currentItem.getQuantity()));
                if (onUpdateListener != null) onUpdateListener.run();
            } else {
                Others.showAlert(lblName, "Sản phẩm đã hết hàng trong kho!", true);
                txtQuantity.setText(String.valueOf(currentItem.getQuantity()));
            }
        }
    }

    @FXML private void handleDelete() {
        if (Others.showCustomConfirm("Xác nhận", "Xóa món này khỏi giỏ hàng?", "Xóa", "Hủy")) {
            int customerId = UserSession.getInstance().isGuest() ? 0 : UserSession.getInstance().getId();
            CartManager.getInstance().removeCustomerCartItem(customerId, currentItem.getProduct());
            if (onUpdateListener != null) onUpdateListener.run();
        }
    }
}
