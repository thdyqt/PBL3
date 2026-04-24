package GUI.Staff;

import BusinessBLL.DeliveryInfoBusiness;
import BusinessBLL.OrderDetailBusiness;
import EntityDTO.Order;
import EntityDTO.OrderDetail;
import Util.Others;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class OrderOnlineDetailController {

    @FXML private Label lblOrderId;
    @FXML private Label lblReceiverName;
    @FXML private Label lblReceiverPhone;
    @FXML private Label lblAddress;
    @FXML private Label lblStatus;
    @FXML private Label lblPayment;

    @FXML private TableView<OrderDetail> tableDetail;
    @FXML private TableColumn<OrderDetail, String> colProductName;
    @FXML private TableColumn<OrderDetail, String> colQuantity;
    @FXML private TableColumn<OrderDetail, String> colPrice;
    @FXML private TableColumn<OrderDetail, String> colTotal;

    @FXML private Label lblSubTotal;
    @FXML private Label lblDiscount;
    @FXML private Label lblFinalTotal;

    public void setOrderData(Order order) {
        if (order == null) return;

        lblOrderId.setText("Chi tiết Đơn hàng #" + order.getId());

        EntityDTO.DeliveryInfo deliveryInfo = DeliveryInfoBusiness.getDeliveryInfo(order.getId());

        if (deliveryInfo != null) {
            lblReceiverName.setText(deliveryInfo.getReceiverName());
            lblReceiverPhone.setText(deliveryInfo.getReceiverPhone());
            lblAddress.setText(deliveryInfo.getDeliveryAddress());
        } else {
            lblReceiverName.setText("Khách vãng lai");
            lblReceiverPhone.setText("N/A");
            lblAddress.setText("N/A");
        }

        if (order.getStatus().name().equals("Cancelled")) {
            lblStatus.setText(ChangeToVie(order.getStatus().name()) + " (Lý do: " + order.getCancelReason() + ")");
            lblStatus.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
        } else {
            lblStatus.setText(ChangeToVie(order.getStatus().name()));
            lblStatus.setStyle("-fx-text-fill: #D4891A; -fx-font-weight: bold;");
        }

        String paymentStr = order.getPayment().name().equals("Card") ? "Chuyển khoản (Card)" : "Tiền mặt (COD)";
        lblPayment.setText(paymentStr);

        lblSubTotal.setText(Others.formatPrice(order.getSubTotal()));
        lblDiscount.setText("- " + Others.formatPrice(order.getDiscountAmount()));
        lblFinalTotal.setText(Others.formatPrice(order.getFinalAmount()));

        colProductName.setCellValueFactory(cellData -> {
            if (cellData.getValue().getProduct() != null) {
                return new SimpleStringProperty(cellData.getValue().getProduct().getProductName());
            }
            return new SimpleStringProperty("Sản phẩm không xác định");
        });

        colQuantity.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));
        colPrice.setCellValueFactory(cellData -> new SimpleStringProperty(Others.formatPrice(cellData.getValue().getPrice())));
        colTotal.setCellValueFactory(cellData -> new SimpleStringProperty(Others.formatPrice(cellData.getValue().getTotalPrice())));

        List<OrderDetail> details = OrderDetailBusiness.getDetailsByOrderId_BLL(order.getId());
        if (details != null && !details.isEmpty()) {
            ObservableList<OrderDetail> list = FXCollections.observableArrayList(details);
            tableDetail.setItems(list);
        }
    }

    public String ChangeToVie(String status){
        switch (status){
            case "Waiting_for_validation" : return "Chờ xác nhận";
            case "Processing" : return "Đang xử lý";
            case "Delivering" : return "Đang giao hàng";
            case "Finished" : return "Đã hoàn thành";
            case "Cancelled" : return "Đã hủy";
            default: return "Không xác định";
        }
    }
}