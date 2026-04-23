package GUI.Customer;

import BusinessBLL.OrderBusiness;
import BusinessBLL.PromoCodeBusiness;
import EntityDTO.Customer;
import EntityDTO.OrderDetail;
import EntityDTO.PromoCode;
import Util.CartManager;
import Util.Others;
import Util.UserSession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerCartController implements Initializable {
    @FXML private AnchorPane mainPane;
    @FXML private VBox vboxCartItems;

    @FXML private Label lblDeliveryInfo;
    @FXML private ComboBox<PromoCode> cbbVoucher;
    @FXML private Label lblSubTotal;
    @FXML private Label lblDiscount;
    @FXML private Label lblTotal;
    @FXML private Button btnCheckout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCartItems();
        setupVoucherComboBox();
        updateOrderSummary();

        if (!UserSession.getInstance().isGuest()) {
            lblDeliveryInfo.setText(UserSession.getInstance().getName() + " - " + UserSession.getInstance().getPhone());
        }
    }

    private void loadCartItems() {
        vboxCartItems.getChildren().clear();

        if (CartManager.getInstance().getCustomerCart().isEmpty()) {
            vboxCartItems.getChildren().add(new Label("Giỏ hàng của bạn đang trống!"));
            btnCheckout.setDisable(true);
            return;
        }

        btnCheckout.setDisable(false);
        try {
            for (OrderDetail item : CartManager.getInstance().getCustomerCart()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Customer/CartItemCard.fxml"));
                Parent cardNode = loader.load();

                CartItemCardController controller = loader.getController();
                controller.setData(item, () -> {
                    loadCartItems();
                    updateOrderSummary();
                });

                vboxCartItems.getChildren().add(cardNode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateOrderSummary() {
        Customer currentCustomer = UserSession.getInstance().getCustomer();
        int subTotal = 0;
        for (OrderDetail item : CartManager.getInstance().getCustomerCart()) {
            subTotal += (item.getPrice() * item.getQuantity());
        }

        lblSubTotal.setText(Others.formatPrice(subTotal));

        int discount = 0;
        PromoCode selectedPromo = null;
        if (cbbVoucher.getSelectionModel().getSelectedIndex() > 0) {
            selectedPromo = cbbVoucher.getSelectionModel().getSelectedItem();
        }

        if (selectedPromo != null && !selectedPromo.getCode().equals("Không có")) {
            if (subTotal < selectedPromo.getMinOrderValue()) {
                Others.showAlert(mainPane, "Đơn hàng chưa đạt giá trị tối thiểu của mã giảm giá (" + Others.formatPrice(selectedPromo.getMinOrderValue()) + ")!", true);
                Platform.runLater(() -> cbbVoucher.getSelectionModel().selectFirst());

                selectedPromo = null;
            }
        }

        discount = OrderBusiness.getDiscountAmount(subTotal, currentCustomer, selectedPromo);
        lblDiscount.setText(Others.formatPrice(discount));

        int total = subTotal - discount;
        lblTotal.setText(Others.formatPrice(total));
    }

    @FXML private void handleClearCart() {
        if (Others.showCustomConfirm("Xác nhận", "Bạn có chắc muốn làm sạch giỏ hàng?", "Đồng ý", "Hủy")) {
            int customerId = UserSession.getInstance().isGuest() ? 0 : UserSession.getInstance().getId();
            CartManager.getInstance().clearCustomerCart(customerId);
            loadCartItems();
            updateOrderSummary();
        }
    }

    @FXML private void handleEditDeliveryInfo() {
        // Mở dialog sửa thông tin cá nhân hoặc địa chỉ
        System.out.println("Mở form sửa địa chỉ...");
    }

    @FXML private void handleCheckout() {
        if (UserSession.getInstance().isGuest()) {
            Others.showAlert(lblTotal, "Vui lòng đăng nhập để thanh toán!", true);
            // Chuyển hướng sang trang Login...
            return;
        }
        // Gọi hàm tạo Order dưới DB
        System.out.println("Thực hiện thanh toán...");
    }

    private void setupVoucherComboBox() {
        List<PromoCode> listCode = PromoCodeBusiness.getAllActivePromoCodes(PromoCode.Type.Online);
        PromoCode allCode = new PromoCode("Không có", "Không có", 0, PromoCode.CodeType.Amount, PromoCode.Type.Offline, 0, null, null, PromoCode.CodeStatus.Active);
        listCode.add(0, allCode);

        cbbVoucher.setItems(FXCollections.observableArrayList(listCode));
        cbbVoucher.getSelectionModel().selectFirst();
    }
}