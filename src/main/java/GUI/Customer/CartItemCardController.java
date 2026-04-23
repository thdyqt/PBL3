package GUI.Customer;

import EntityDTO.OrderDetail;
import Util.CartManager;
import Util.Others;
import Util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class CartItemCardController {
    @FXML private ImageView imgProduct;
    @FXML private Label lblName;
    @FXML private Label lblPrice;
    @FXML private Label lblQuantity;

    private OrderDetail currentItem;
    private Runnable onUpdateListener;

    public void setData(OrderDetail item, Runnable onUpdateListener) {
        this.currentItem = item;
        this.onUpdateListener = onUpdateListener;

        lblName.setText(item.getProduct().getProductName());
        lblPrice.setText(Others.formatPrice(item.getPrice()));
        lblQuantity.setText(item.getQuantity() + "");
        Others.loadImage(item.getProduct().getImage(), imgProduct, 80, 80);
    }

    @FXML private void handlePlus() {
        int customerId = UserSession.getInstance().isGuest() ? 0 : UserSession.getInstance().getId();
        boolean success = CartManager.getInstance().addToCustomerCart(customerId, currentItem.getProduct(), 1);
        if (success) {
            lblQuantity.setText(String.valueOf(currentItem.getQuantity()));
            if (onUpdateListener != null) onUpdateListener.run();
        } else {
            Others.showAlert(lblName, "Sản phẩm đã hết hàng trong kho!", true);
        }
    }

    @FXML private void handleMinus() {
        if (currentItem.getQuantity() > 1) {
            int customerId = UserSession.getInstance().isGuest() ? 0 : UserSession.getInstance().getId();
            CartManager.getInstance().addToCustomerCart(customerId, currentItem.getProduct(), -1);
            lblQuantity.setText(String.valueOf(currentItem.getQuantity()));
            if (onUpdateListener != null) onUpdateListener.run();
        } else {
            handleDelete();
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
