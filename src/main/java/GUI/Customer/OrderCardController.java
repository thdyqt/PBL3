package GUI.Customer;

import BusinessBLL.OrderBusiness;
import BusinessBLL.OrderDetailBusiness;
import DataDAL.ProductData;
import EntityDTO.Order;
import EntityDTO.OrderDetail;
import GUI.OrderOnlineDetailController;
import Util.Others;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class OrderCardController implements Initializable {
    @FXML private Label  lblOrderID;
    @FXML private Label  lblOrderDate;
    @FXML private Label  lblStatus;
    @FXML private Label  lblProductNames;
    @FXML private HBox   imageContainer;
    @FXML private Label  lblItemCount;
    @FXML private Label  lblTotal;

    @FXML private Button btnDetail;
    @FXML private Button btnConfirmReceive;
    @FXML private Button btnReportIssue;

    private Order      currentOrder;
    private StackPane  contentArea;
    private Runnable reloadCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Others.playButtonAnimation(btnDetail);
    }

    public void setOrder(Order order, StackPane contentArea, Runnable reloadCallback) {
        this.currentOrder = order;
        this.contentArea  = contentArea;
        this.reloadCallback = reloadCallback;
        render();

        if (order.getType() == Order.OrderType.Online && order.getStatus() == Order.OrderStatus.Delivered) {
            btnConfirmReceive.setVisible(true);
            btnConfirmReceive.setManaged(true);

            btnReportIssue.setVisible(true);
            btnReportIssue.setManaged(true);

            lblStatus.setText("Shipper báo đã giao. Vui lòng xác nhận!");
            lblStatus.setStyle("-fx-text-fill: #EAB308;");

        } else {
            btnConfirmReceive.setVisible(false);
            btnConfirmReceive.setManaged(false);

            btnReportIssue.setVisible(false);
            btnReportIssue.setManaged(false);
        }
    }

    private void render() {
        lblOrderID.setText("#DH" + String.format("%03d", currentOrder.getId()));
        lblOrderDate.setText(currentOrder.getOrderTime()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        renderStatus();
        lblTotal.setText(Others.formatPrice(currentOrder.getFinalAmount()));

        Thread bgThread = new Thread(() -> {
            List<OrderDetail> details = currentOrder.getOrderDetail();
            if (details == null || details.isEmpty()) {
                details = OrderDetailBusiness.getDetailsByOrderId_BLL(currentOrder.getId());
            }
            final List<OrderDetail> finalDetails = details;

            Platform.runLater(() -> {
                if (finalDetails != null && !finalDetails.isEmpty()) {
                    String names = finalDetails.stream()
                            .map(d -> d.getProduct().getProductName())
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("");
                    lblProductNames.setText(names);

                    int totalItems = finalDetails.stream().mapToInt(OrderDetail::getQuantity).sum();
                    lblItemCount.setText(totalItems + " món");

                    loadImagesAsync(finalDetails);
                }
            });
        });
        bgThread.setDaemon(true);
        bgThread.start();
    }

    private void renderStatus() {
        String text;
        String style;

        switch (currentOrder.getStatus()) {
            case Waiting_for_validation -> {
                text  = "⏳ Chờ xác nhận";
                style = "-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 14; -fx-background-radius: 20;";
            }
            case Processing -> {
                text  = "👨‍🍳 Đang xử lý";
                style = "-fx-background-color: #EFF6FF; -fx-text-fill: #2563EB; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 14; -fx-background-radius: 20;";
            }
            case Delivering -> {
                text  = "🚗 Đang giao";
                style = "-fx-background-color: #F3E8FF; -fx-text-fill: #7E22CE; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 14; -fx-background-radius: 20;";
            }
            case Delivered -> {
                text  = "📍 Đã giao đến";
                style = "-fx-background-color: #E0F2FE; -fx-text-fill: #0284C7; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 14; -fx-background-radius: 20;";
            }
            case Reported -> {
                text  = "⚠️ Đang khiếu nại";
                style = "-fx-background-color: #FEF2F2; -fx-text-fill: #991B1B; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 14; -fx-background-radius: 20;";
            }
            case Finished -> {
                text  = "✅ Hoàn thành";
                style = "-fx-background-color: #D1FAE5; -fx-text-fill: #059669; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 14; -fx-background-radius: 20;";
            }
            case Cancelled -> {
                text  = "❌ Đã hủy";
                style = "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 14; -fx-background-radius: 20;";
            }
            default -> {
                text  = "Không rõ";
                style = "";
            }
        }

        lblStatus.setText(text);
        lblStatus.setStyle(style);
    }

    private void loadImagesAsync(List<OrderDetail> actualDetails) {
        imageContainer.getChildren().clear();

        Thread imgThread = new Thread(() -> {
            for (OrderDetail detail : actualDetails) {
                int productID = detail.getProduct().getProductID();
                String imageName = ProductData.getImage(productID); // Nặng vì Query DB

                Image image = null;
                try {
                    var stream = (imageName != null)
                            ? getClass().getResourceAsStream("/images/" + imageName)
                            : getClass().getResourceAsStream("/images/default.png");

                    if (stream != null) {
                        image = new Image(stream, 80, 80, true, true);
                    }
                } catch (Exception e) {}

                final Image finalImg = image;
                Platform.runLater(() -> {
                    ImageView iv = new ImageView();
                    iv.setFitWidth(80);
                    iv.setFitHeight(80);
                    iv.setPreserveRatio(true);
                    iv.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);");

                    if (finalImg != null) {
                        iv.setImage(finalImg);
                    }

                    Tooltip tip = new Tooltip(detail.getProduct().getProductName() + " x" + detail.getQuantity());
                    Tooltip.install(iv, tip);
                    imageContainer.getChildren().add(iv);
                });
            }
        });
        imgThread.setDaemon(true);
        imgThread.start();
    }


    @FXML
    private void handleDetail() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/GUI/OrderOnlineDetail.fxml")
            );
            Parent root = loader.load();

            OrderOnlineDetailController ctrl = loader.getController();
            ctrl.setOrderData(currentOrder);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Chi tiết đơn hàng #" + currentOrder.getId());
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            System.err.println("Lỗi mở chi tiết đơn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleConfirmReceive() {
        if (Others.showCustomConfirm("Xác nhận nhận hàng", "Bạn đã nhận được hàng? Vui lòng chỉ xác nhận khi bạn đã cầm sản phẩm trên tay.","Xác nhận","Hủy")){
            String result = OrderBusiness.updateOrder(currentOrder, Order.OrderStatus.Finished);

            if (result.contains("thành công")) {
                Others.showAlert(lblOrderID, "Cảm ơn bạn! Đơn hàng đã hoàn thành.", false);
                if (reloadCallback != null) reloadCallback.run();
            } else {
                Others.showAlert(lblOrderID, "Lỗi: " + result, true);
            }
        }
    }

    @FXML
    private void handleReportIssue() {
        if (Others.showCustomConfirm("Báo cáo sự cố giao hàng", "Hệ thống sẽ tạm ngưng đơn hàng này và nhân viên cửa hàng sẽ gọi điện cho bạn ngay lập tức để hỗ trợ. Bạn có chắc chắn muốn báo cáo?","Xác nhận","Hủy")){
            String result = OrderBusiness.updateOrder(currentOrder, Order.OrderStatus.Reported);

            if (result.contains("thành công")) {
                Others.showAlert(lblOrderID, "Đã gửi báo cáo! Nhân viên cửa hàng sẽ liên hệ với bạn trong ít phút tới.", false);

                if (reloadCallback != null) {
                    reloadCallback.run();
                }
            } else {
                Others.showAlert(lblOrderID, "Lỗi khi gửi báo cáo: " + result, true);
            }
        }
    }
}