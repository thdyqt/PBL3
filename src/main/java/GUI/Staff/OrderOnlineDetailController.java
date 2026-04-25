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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderOnlineDetailController {
    @FXML private Label lblOrderId;
    @FXML private Label lblReceiverName;
    @FXML private Label lblReceiverPhone;
    @FXML private Label lblAddress;
    @FXML private Label lblStatus;
    @FXML private Label lblPayment;
    @FXML private Label lblOrderTime;

    @FXML private TableView<OrderDetail> tableDetail;
    @FXML private TableColumn<OrderDetail, Void> col_STT;
    @FXML private TableColumn<OrderDetail, String> colImage;
    @FXML private TableColumn<OrderDetail, String> colProductName;
    @FXML private TableColumn<OrderDetail, String> colQuantity;
    @FXML private TableColumn<OrderDetail, String> colPrice;
    @FXML private TableColumn<OrderDetail, String> colTotal;

    @FXML private Label lblTotalItems;
    @FXML private Label lblSubTotal;
    @FXML private Label lblDiscount;
    @FXML private Label lblFinalTotal;

    @FXML
    public void initialize() {
        setupTable();
    }

    private void setupTable() {
        col_STT.setCellFactory(column -> new TableCell<OrderDetail, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        colImage.setCellValueFactory(cellData -> {
            if (cellData.getValue().getProduct() != null) {
                return new SimpleStringProperty(cellData.getValue().getProduct().getImage());
            }
            return new SimpleStringProperty("");
        });
        colImage.setCellFactory(c -> new TableCell<>() {
            private final ImageView iv = new ImageView();
            @Override protected void updateItem(String imgName, boolean empty) {
                super.updateItem(imgName, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                iv.setFitWidth(50);
                iv.setFitHeight(50);
                iv.setPreserveRatio(true);

                Others.loadImage(imgName, iv, 50, 50);
                setGraphic(iv);
            }
        });

        colProductName.setCellValueFactory(cellData -> {
            if (cellData.getValue().getProduct() != null) {
                return new SimpleStringProperty(cellData.getValue().getProduct().getProductName());
            }
            return new SimpleStringProperty("Sản phẩm không xác định");
        });

        colQuantity.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));
        colPrice.setCellValueFactory(cellData -> new SimpleStringProperty(Others.formatPrice(cellData.getValue().getPrice())));
        colTotal.setCellValueFactory(cellData -> new SimpleStringProperty(Others.formatPrice(cellData.getValue().getTotalPrice())));
    }

    public void setOrderData(Order order) {
        if (order == null) return;

        lblOrderId.setText("Chi tiết Đơn hàng #" + order.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (order.getOrderTime() != null) {
            lblOrderTime.setText(order.getOrderTime().format(formatter));
        }

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

        List<OrderDetail> details = OrderDetailBusiness.getDetailsByOrderId_BLL(order.getId());

        int totalQty = 0;
        if (details != null && !details.isEmpty()) {
            ObservableList<OrderDetail> list = FXCollections.observableArrayList(details);
            tableDetail.setItems(list);

            for (OrderDetail d : details) {
                totalQty += d.getQuantity();
            }
        }

        lblTotalItems.setText(String.valueOf(totalQty));
        lblSubTotal.setText(Others.formatPrice(order.getSubTotal()));
        lblDiscount.setText("- " + Others.formatPrice(order.getDiscountAmount()));
        lblFinalTotal.setText(Others.formatPrice(order.getFinalAmount()));
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