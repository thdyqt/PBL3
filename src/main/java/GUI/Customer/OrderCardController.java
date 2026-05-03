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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderCardController {
    @FXML private Label  lblOrderID;
    @FXML private Label  lblOrderDate;
    @FXML private Label  lblStatus;
    @FXML private Label  lblProductNames;
    @FXML private HBox   imageContainer;
    @FXML private Label  lblItemCount;
    @FXML private Label  lblTotal;
    @FXML private Button btnCancel;
    @FXML private Button btnDetail;

    private Order      currentOrder;
    private StackPane  contentArea;

    public void setOrder(Order order, StackPane contentArea) {
        this.currentOrder = order;
        this.contentArea  = contentArea;
        render();
    }

    private void render() {
        lblOrderID.setText("#DH" + String.format("%03d", currentOrder.getId()));
        lblOrderDate.setText(currentOrder.getOrderTime()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        renderStatus();
        lblTotal.setText(Others.formatPrice(currentOrder.getFinalAmount()));

        if (currentOrder.getStatus() == Order.OrderStatus.Waiting_for_validation) {
            btnCancel.setVisible(true);
            btnCancel.setManaged(true);
        } else {
            btnCancel.setVisible(false);
            btnCancel.setManaged(false);
        }

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
    private void handleCancel() {
        Order latestOrder = OrderBusiness.getAllOrder().stream()
                .filter(o -> o.getId() == currentOrder.getId())
                .findFirst()
                .orElse(null);

        if (latestOrder == null || latestOrder.getStatus() != Order.OrderStatus.Waiting_for_validation) {
            StackPane targetPane = this.contentArea;
            if (targetPane == null && btnCancel.getScene().getRoot() instanceof StackPane) {
                targetPane = (StackPane) btnCancel.getScene().getRoot();
            }

            Others.showAlert(targetPane, "Rất tiếc! Đơn hàng này đã được nhân viên tiếp nhận và xử lý, bạn không thể hủy nữa.", true);

            if (latestOrder != null) {
                currentOrder.setStatus(latestOrder.getStatus());
                render();
            }
            return;
        }

        Window ownerWindow = btnCancel.getScene().getWindow();
        String reason = Others.showCancelReasonDialog(ownerWindow, String.valueOf(currentOrder.getId()));

        if (reason != null && !reason.trim().isEmpty()) {
            String msg = OrderBusiness.cancelOnlineOrder(currentOrder, reason);

            if (msg.contains("thành công")) {
                currentOrder.setStatus(Order.OrderStatus.Cancelled);

                StackPane targetPane = this.contentArea;
                if (targetPane == null && btnCancel.getScene().getRoot() instanceof StackPane) {
                    targetPane = (StackPane) btnCancel.getScene().getRoot();
                }
                Others.showAlert(targetPane, msg, false);
                render();
            } else {
                StackPane targetPane = this.contentArea;
                if (targetPane == null && btnCancel.getScene().getRoot() instanceof StackPane) {
                    targetPane = (StackPane) btnCancel.getScene().getRoot();
                }
                Others.showAlert(targetPane, msg, true);
            }
        }
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
}