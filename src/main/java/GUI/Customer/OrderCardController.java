package GUI.Customer;

import BusinessBLL.OrderBusiness;
import BusinessBLL.OrderDetailBusiness;
import DataDAL.ProductData;
import EntityDTO.Order;
import EntityDTO.OrderDetail;
import GUI.Staff.OrderOnlineDetailController;
import Util.Others;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderCardController {
    // ===== FXML COMPONENTS =====
    @FXML private Label  lblOrderID;
    @FXML private Label  lblOrderDate;
    @FXML private Label  lblStatus;
    @FXML private Label  lblProductNames;
    @FXML private HBox   imageContainer;
    @FXML private Label  lblItemCount;
    @FXML private Label  lblTotal;
    @FXML private Button btnCancel;
    @FXML private Button btnDetail;

    // ===== DATA =====
    private Order      currentOrder;
    private StackPane  contentArea;

    // ===== NHẬN DỮ LIỆU TỪ MyOrderController =====
    public void setOrder(Order order, StackPane contentArea) {
        this.currentOrder = order;
        this.contentArea  = contentArea;
        render();
    }

    // ===== RENDER CARD =====
    private void render() {
        // Mã đơn
        lblOrderID.setText("#DH" + String.format("%03d", currentOrder.getId()));

        // Ngày đặt
        lblOrderDate.setText(currentOrder.getOrderTime()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        // Trạng thái
        renderStatus();

        // Sản phẩm
        List<OrderDetail> details = currentOrder.getOrderDetail();
        if (details == null || details.isEmpty()) {
            details = OrderDetailBusiness.getDetailsByOrderId_BLL(currentOrder.getId());
        }

        if (details != null && !details.isEmpty()) {
            String names = details.stream()
                    .map(d -> d.getProduct().getProductName())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            lblProductNames.setText(names);

            int totalItems = details.stream().mapToInt(OrderDetail::getQuantity).sum();
            lblItemCount.setText(totalItems + " món");

            loadImages(details);
        }

        // Tổng tiền
        lblTotal.setText(Others.formatPrice(currentOrder.getFinalAmount()));

        // Nút Hủy — chỉ hiện khi Chờ xác nhận
        if (currentOrder.getStatus() == Order.OrderStatus.Waiting_for_validation) {
            btnCancel.setVisible(true);
            btnCancel.setManaged(true);
        }
    }

    // ===== RENDER TRẠNG THÁI =====
    private void renderStatus() {
        String text;
        String style;

        switch (currentOrder.getStatus()) {
            case Waiting_for_validation -> {
                text  = "⏳ Chờ xác nhận";
                style = "-fx-background-color: #FFF8EE; -fx-text-fill: #D4891A; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 12; -fx-border-color: #F2B950; -fx-border-width: 1; -fx-border-radius: 12;";
            }
            case Processing -> {
                text  = "👨‍🍳 Đang chuẩn bị";
                style = "-fx-background-color: #F0F8FF; -fx-text-fill: #1565C0; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 12; -fx-border-color: #90CAF9; -fx-border-width: 1; -fx-border-radius: 12;";
            }
            case Delivering -> {
                text  = "🚗 Đang giao";
                style = "-fx-background-color: #F3F0FF; -fx-text-fill: #6A1B9A; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 12; -fx-border-color: #CE93D8; -fx-border-width: 1; -fx-border-radius: 12;";
            }
            case Finished -> {
                text  = "✅ Hoàn thành";
                style = "-fx-background-color: #F1FFF3; -fx-text-fill: #2E7D32; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 12; -fx-border-color: #A5D6A7; -fx-border-width: 1; -fx-border-radius: 12;";
            }
            case Cancelled -> {
                text  = "❌ Đã hủy";
                style = "-fx-background-color: #FFF5F5; -fx-text-fill: #C62828; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 12; -fx-border-color: #EF9A9A; -fx-border-width: 1; -fx-border-radius: 12;";
            }
            default -> {
                text  = "Không rõ";
                style = "";
            }
        }

        lblStatus.setText(text);
        lblStatus.setStyle(style);
    }

    // ===== LOAD ẢNH SCROLL NGANG =====
    private void loadImages(List<OrderDetail> details) {
        imageContainer.getChildren().clear();

        // ✅ Nếu details rỗng → gọi DB lấy lại
        List<OrderDetail> actualDetails = details;
        if (actualDetails == null || actualDetails.isEmpty()) {
            actualDetails = OrderDetailBusiness.getDetailsByOrderId_BLL(currentOrder.getId());
        }

        if (actualDetails == null || actualDetails.isEmpty()) {
            System.out.println("Không có sản phẩm trong đơn #" + currentOrder.getId());
            return;
        }

        for (OrderDetail detail : actualDetails) {
            int productID = detail.getProduct().getProductID();
            String imageName = ProductData.getImage(productID);

            ImageView iv = new ImageView();
            iv.setFitWidth(80);
            iv.setFitHeight(80);
            iv.setPreserveRatio(true);
            iv.setStyle("-fx-border-color: #EEE5D8; -fx-border-width: 1;");

            try {
                var stream = (imageName != null)
                        ? getClass().getResourceAsStream("/images/" + imageName)
                        : getClass().getResourceAsStream("/images/default.png");

                if (stream != null) {
                    iv.setImage(new Image(stream));
                }
            } catch (Exception e) {
                System.err.println("Lỗi load ảnh: " + e.getMessage());
            }

            Tooltip tip = new Tooltip(
                    detail.getProduct().getProductName() + " x" + detail.getQuantity()
            );
            Tooltip.install(iv, tip);
            imageContainer.getChildren().add(iv);
        }
    }

    // ===== HỦY ĐƠN =====
    @FXML
    private void handleCancel() {
        Window ownerWindow = btnCancel.getScene().getWindow();
        String orderCode = "#DH" + String.format("%03d", currentOrder.getId());
        String reason = Others.showCancelReasonDialog(ownerWindow, orderCode);
        if (reason != null) {
            String result = OrderBusiness.cancelOnlineOrder(currentOrder, reason);

            if (result.contains("thành công")) {
                showAlert("✅ " + result);

                btnCancel.setVisible(false);
                btnCancel.setManaged(false);
                renderStatus();
            } else {
                showAlert("❌ " + result);
            }
        }
    }

    // ===== XEM CHI TIẾT =====
    @FXML
    private void handleDetail() {
        // TODO: Điền tên file fxml và hàm nhận dữ liệu khi bạn cho biết
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/GUI/Customer/OrderDetail.fxml") // ← sửa đường dẫn
            );
            VBox view = loader.load();

            // TODO: Điền tên Controller và hàm nhận Order
            OrderOnlineDetailController ctrl = loader.getController();
            ctrl.setOrderData(currentOrder);

            if (contentArea != null) {
                contentArea.getChildren().setAll(view);
            }
        } catch (Exception e) {
            System.err.println("Lỗi mở chi tiết đơn: " + e.getMessage());
        }
    }

    // ===== HELPER =====
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}