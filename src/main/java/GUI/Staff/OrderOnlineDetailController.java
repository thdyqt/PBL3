package GUI.Staff;

import BusinessBLL.DeliveryInfoBusiness;
import EntityDTO.Order;
import EntityDTO.OrderDetail;
import BusinessBLL.OrderDetailBusiness;
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

    @FXML private Label lblCustomerInfo;
    @FXML private Label lblAddress;
    @FXML private Label lblStatus;
    @FXML private Label lblFinalTotal;
    @FXML private Label lblPromoCode;
    @FXML private Label lblDiscount;

    @FXML private TableView<OrderDetail> tableDetail;
    @FXML private TableColumn<OrderDetail, String> colProductName;
    @FXML private TableColumn<OrderDetail, String> colQuantity;
    @FXML private TableColumn<OrderDetail, String> colPrice;
    @FXML private TableColumn<OrderDetail, String> colTotal;

    public void setOrderData(Order order) {
        if (order == null) return;

        EntityDTO.DeliveryInfo deliveryInfo = DeliveryInfoBusiness.getDeliveryInfo(order.getId());

        if (deliveryInfo != null) {
            lblCustomerInfo.setText("Người nhận: " + deliveryInfo.getReceiverName() + " - SĐT: " + deliveryInfo.getReceiverPhone());
            lblAddress.setText("Địa chỉ: " + deliveryInfo.getDeliveryAddress());
        } else {
            lblCustomerInfo.setText("Người nhận: Không xác định - SĐT: N/A");
            lblAddress.setText("Địa chỉ: Không xác định");
        }

        if(order.getStatus().name().equals("Cancelled")){
            lblStatus.setText("Trạng thái đơn: " + ChangeToVie(order.getStatus().name()) + " | Lí do hủy: " + order.getCancelReason());
        } else {
            lblStatus.setText("Trạng thái đơn: " + ChangeToVie(order.getStatus().name()) + " | Thanh toán: " + order.getPayment().name());
        }

        lblPromoCode.setText("Mã giảm giá: " + (order.getAppliedCode() != null ? order.getAppliedCode() : "Không có"));
        lblDiscount.setText("Số tiền được giảm: " + Others.formatPrice(order.getDiscountAmount()));

        colProductName.setCellValueFactory(cellData -> {
            if (cellData.getValue().getProduct() != null) {
                return new SimpleStringProperty(cellData.getValue().getProduct().getProductName());
            }
            return new SimpleStringProperty("Sản phẩm không xác định");
        });

        colQuantity.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));
        colPrice.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%,d đ", cellData.getValue().getPrice())));
        colTotal.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%,d đ", cellData.getValue().getTotalPrice())));

        List<OrderDetail> details = OrderDetailBusiness.getDetailsByOrderId_BLL(order.getId());

        if (details != null && !details.isEmpty()) {
            ObservableList<OrderDetail> list = FXCollections.observableArrayList(details);
            tableDetail.setItems(list);


            lblFinalTotal.setText(String.format("Tổng tiền thanh toán: %,d VNĐ", order.getFinalAmount()));
        } else {
            lblFinalTotal.setText("Tổng tiền thanh toán: 0 VNĐ");
        }
    }

    public String ChangeToVie(String status){
        switch (status){
            case "Waiting_for_validation" : return "Chờ xác nhận";
            case "Processing" : return "Đang xử lí";
            case "Delivering" : return "Đang giao hàng";
            case "Finished" : return "Đã hoàn thành";
            case "Cancelled" : return "Đã hủy";
            default: return "Không xác định";
        }
    }
}