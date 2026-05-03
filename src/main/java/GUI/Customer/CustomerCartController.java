package GUI.Customer;

import BusinessBLL.OrderBusiness;
import BusinessBLL.PromoCodeBusiness;
import EntityDTO.*;
import Util.CartManager;
import Util.Others;
import Util.UserSession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerCartController implements Initializable {
    @FXML private AnchorPane mainPane;
    @FXML private VBox vboxCartItems;

    @FXML private Label lblDeliveryInfo;
    @FXML private ComboBox<PromoCode> cbbVoucher;
    @FXML private ComboBox<String> cbbPaymentMethod;
    @FXML private Label lblShippingFee;
    @FXML private Label lblSubTotal;
    @FXML private Label lblDiscount;
    @FXML private Label lblTotal;
    @FXML private Button btnCheckout;

    private final int SHIPPING_FEE = 15000;
    private String deliveryName = "";
    private String deliveryPhone = "";
    private String deliveryAddress = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!UserSession.getInstance().isGuest()) {
            UserSession session = UserSession.getInstance();
            deliveryName = session.getName() != null ? session.getName() : "";
            deliveryPhone = session.getPhone() != null ? session.getPhone() : "";
            deliveryAddress = session.getAddress() != null ? session.getAddress() : "";
        }

        loadCartItems();
        setupVoucherComboBox();
        setupPaymentComboBox();
        updateOrderSummary();
        updateDeliveryInfoUI();
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
        if (CartManager.getInstance().getCustomerCart().isEmpty()) {
            lblSubTotal.setText("0đ");
            lblDiscount.setText("0đ");
            lblTotal.setText("0đ");
            if (lblShippingFee != null) lblShippingFee.setText("0đ");

            Platform.runLater(() -> cbbVoucher.getSelectionModel().selectFirst());
            return;
        }
        else lblShippingFee.setText("15.000đ");

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

        int total = subTotal - discount + SHIPPING_FEE;
        lblTotal.setText(Others.formatPrice(total));
    }

    private void setupVoucherComboBox() {
        List<PromoCode> listCode = PromoCodeBusiness.getAllActivePromoCodes(PromoCode.Type.Online);
        PromoCode allCode = new PromoCode("Không có", "Không có", 0, PromoCode.CodeType.Amount, PromoCode.Type.Offline, 0, null, null, PromoCode.CodeStatus.Active);
        listCode.add(0, allCode);

        cbbVoucher.setItems(FXCollections.observableArrayList(listCode));
        cbbVoucher.getSelectionModel().selectFirst();

        cbbVoucher.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateOrderSummary();
            }
        });
    }

    private void setupPaymentComboBox() {
        cbbPaymentMethod.getItems().addAll("Thanh toán khi nhận hàng (COD)", "Chuyển khoản ngân hàng");
        cbbPaymentMethod.getSelectionModel().selectFirst();
    }

    @FXML private void handleClearCart() {
        if (Others.showCustomConfirm("Xác nhận", "Bạn có chắc muốn làm sạch giỏ hàng?", "Đồng ý", "Hủy")) {
            int customerId = UserSession.getInstance().isGuest() ? 0 : UserSession.getInstance().getId();
            CartManager.getInstance().clearCustomerCart(customerId);
            loadCartItems();
            updateOrderSummary();
        }
    }

    private void updateDeliveryInfoUI() {
        String nameStr = deliveryName.isEmpty() ? (UserSession.getInstance().isGuest() ? "Khách mua lẻ" : "Chưa có tên") : deliveryName;
        String phoneStr = deliveryPhone.isEmpty() ? "Chưa có SĐT" : deliveryPhone;
        String addressStr = deliveryAddress.isEmpty() ? "Chưa có địa chỉ (Vui lòng cập nhật)" : deliveryAddress;

        lblDeliveryInfo.setText(nameStr + " - " + phoneStr + "\n📍 " + addressStr);
    }

    @FXML private void handleEditDeliveryInfo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Customer/GuestDeliveryDialog.fxml"));
            Parent root = loader.load();

            GuestDeliveryDialogController controller = loader.getController();

            controller.setData(deliveryName, deliveryPhone, deliveryAddress);

            Stage stage = new Stage();
            stage.setTitle("Thông tin nhận hàng");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            if (controller.isSaveSuccess()) {
                deliveryName = controller.getGuestName();
                deliveryPhone = controller.getGuestPhone();
                deliveryAddress = controller.getGuestAddress();
                updateDeliveryInfoUI();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleCheckout() {
        if (CartManager.getInstance().getCustomerCart().isEmpty()) {
            Others.showAlert(mainPane, "Giỏ hàng của bạn đang trống!", true);
            return;
        }

        if (deliveryName.isEmpty() || deliveryPhone.isEmpty() || deliveryAddress.isEmpty()) {
            Others.showAlert(mainPane, "Vui lòng cập nhật đầy đủ thông tin người nhận!", true);
            handleEditDeliveryInfo();
            return;
        }

        if (!Others.showCustomConfirm("Xác nhận thanh toán", "Bạn có chắc chắn muốn tiến hành đặt đơn hàng này không?", "Xác nhận", "Trở lại")) {
            return;
        }

        String paymentMethod = cbbPaymentMethod.getValue() != null ? cbbPaymentMethod.getValue() : "";
        Customer currentCustomer = UserSession.getInstance().isGuest() ? null : UserSession.getInstance().getCustomer();
        Order.OrderPayment payment = paymentMethod.contains("Chuyển khoản") ? Order.OrderPayment.Card : Order.OrderPayment.Cash;

        int subtotal = 0;
        for (OrderDetail item : CartManager.getInstance().getCustomerCart()) {
            subtotal += (item.getPrice() * item.getQuantity());
        }

        PromoCode selectedPromo = null;
        String code = "";
        if (cbbVoucher.getSelectionModel().getSelectedIndex() > 0) {
            selectedPromo = cbbVoucher.getSelectionModel().getSelectedItem();
            if (selectedPromo != null) {
                code = selectedPromo.getCode();
            }
        }

        int discountAmount = OrderBusiness.getDiscountAmount(subtotal, currentCustomer, selectedPromo);
        int finalTotal = subtotal - discountAmount + SHIPPING_FEE;

        Order newOrder = new Order(
                LocalDateTime.now(),
                null,
                currentCustomer,
                new ArrayList<>(CartManager.getInstance().getCustomerCart()),
                Order.OrderStatus.Waiting_for_validation,
                Order.OrderType.Online,
                payment,
                subtotal,
                code,
                discountAmount,
                finalTotal,
                ""
        );

        DeliveryInfo deliveryInfo = new DeliveryInfo(0, deliveryName, deliveryPhone, deliveryAddress);

        if (payment == Order.OrderPayment.Card) {
            Others.showVietQR(finalTotal, "Thanh toán đơn hàng DUT Bakery Online", "✅ TÔI ĐÃ CHUYỂN KHOẢN", mainPane, () -> {
                int orderId = OrderBusiness.createOrder(newOrder, deliveryInfo);
                if (orderId > 0) {
                    showSuccessScreen(orderId);
                } else {
                    Others.showAlert(mainPane, "Có lỗi xảy ra khi tạo đơn hàng!", true);
                }
            });
        } else {
            int orderId = OrderBusiness.createOrder(newOrder, deliveryInfo);
            if (orderId > 0) {
                showSuccessScreen(orderId);
            } else {
                Others.showAlert(mainPane, "Có lỗi xảy ra khi tạo đơn hàng!", true);
            }
        }
    }

    private void showSuccessScreen(int orderId) {
        int customerId = UserSession.getInstance().isGuest() ? 0 : UserSession.getInstance().getId();
        CartManager.getInstance().clearCustomerCart(customerId);
        loadCartItems();
        updateOrderSummary();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Customer/OrderSuccessDialog.fxml"));
            Parent root = loader.load();

            OrderSuccessDialogController controller = loader.getController();
            controller.setOrderId(orderId + "");

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Đặt hàng thành công");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            if (controller.isViewOrderSelected()) {
                CustomerDashboardForm.instance.switchForm("/GUI/Customer/MyOrderView.fxml");
                CustomerDashboardForm.instance.setActiveMenu(CustomerDashboardForm.instance.getBtnOrders());
            } else {
                CustomerDashboardForm.instance.switchForm("/GUI/Customer/CustomerProduct.fxml");
                CustomerDashboardForm.instance.setActiveMenu(CustomerDashboardForm.instance.getBtnProducts());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}