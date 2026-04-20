package GUI.Staff;

import EntityDTO.Order;
import EntityDTO.OrderDetail;
import BusinessBLL.OrderDetailBusiness;
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
    @FXML private Label lblFinalTotal;

    @FXML private TableView<OrderDetail> tableDetail;
    @FXML private TableColumn<OrderDetail, String> colProductName;
    @FXML private TableColumn<OrderDetail, String> colQuantity;
    @FXML private TableColumn<OrderDetail, String> colPrice;
    @FXML private TableColumn<OrderDetail, String> colTotal;

    public void setOrderData(Order order) {
        if (order == null) return;

        // 1. Hiển thị thông tin chung của đơn hàng
        if(order.getCustomer() != null) {
            lblCustomerInfo.setText("Khách hàng: " + order.getCustomer().getName() + " - SĐT: " + order.getCustomer().getPhone());
        } else {
            lblCustomerInfo.setText("Khách hàng: Khách vãng lai - SĐT: N/A");
        }

        lblAddress.setText("Trạng thái đơn: " + order.getStatus().name() + " | Thanh toán: " + order.getPayment().name());

        // 2. Cài đặt các cột cho TableView
        // Lưu ý: Dựa theo OrderDetailData của bạn, Product dùng thuộc tính ProductName
        colProductName.setCellValueFactory(cellData -> {
            if (cellData.getValue().getProduct() != null) {
                return new SimpleStringProperty(cellData.getValue().getProduct().getProductName());
            }
            return new SimpleStringProperty("Sản phẩm không xác định");
        });

        colQuantity.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));
        colPrice.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%,d đ", cellData.getValue().getPrice())));
        colTotal.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%,d đ", cellData.getValue().getTotalPrice())));

        // ---------------------------------------------------------
        // 3. ĐIỂM QUAN TRỌNG: GỌI BLL ĐỂ LẤY DỮ LIỆU CHI TIẾT
        // ---------------------------------------------------------
        List<OrderDetail> details = OrderDetailBusiness.getDetailsByOrderId_BLL(order.getId());

        // 4. Đổ dữ liệu vào bảng và tính tổng tiền
        if (details != null && !details.isEmpty()) {
            ObservableList<OrderDetail> list = FXCollections.observableArrayList(details);
            tableDetail.setItems(list);

            // Tính tổng tiền dựa trên các record lấy được từ Database
            int sum = details.stream().mapToInt(OrderDetail::getTotalPrice).sum();
            lblFinalTotal.setText(String.format("Tổng tiền thanh toán: %,d VNĐ", sum));
        } else {
            // Nếu đơn hàng chưa có món nào (lỗi logic khi tạo) hoặc không tìm thấy
            lblFinalTotal.setText("Tổng tiền thanh toán: 0 VNĐ");
        }
    }
}