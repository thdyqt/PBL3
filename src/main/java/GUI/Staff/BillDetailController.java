package GUI.Staff;

import BusinessBLL.OrderDetailBusiness;
import EntityDTO.Order;
import EntityDTO.OrderDetail;
import Util.Others;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillDetailController {

    @FXML private Label lblOrderId;
    @FXML private Label lblReceiverName;
    @FXML private Label lblReceiverPhone;
    @FXML private Label lblStaffName;
    @FXML private Label lblStaffPhone;
    @FXML private Label lblProcessTime;
    @FXML private Label lblPayment;

    @FXML private TableView<OrderDetail> tbOrderDetail;
    @FXML private TableColumn<OrderDetail, Void> col_STT;
    @FXML private TableColumn<OrderDetail, String> colImage;
    @FXML private TableColumn<OrderDetail, String> col_ItemName;
    @FXML private TableColumn<OrderDetail, String> col_ItemQuantity;
    @FXML private TableColumn<OrderDetail, String> col_Price;
    @FXML private TableColumn<OrderDetail, String> col_TotalPrice;

    @FXML private Label lblTotalItems;
    @FXML private Label lblSubTotal;
    @FXML private Label lblDiscount;
    @FXML private Label lblFinalTotal;

    @FXML
    private void initialize() {
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
                    setStyle("-fx-alignment: CENTER;");
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

        col_ItemName.setCellValueFactory(cellData -> {
            if (cellData.getValue().getProduct() != null) {
                return new SimpleStringProperty(cellData.getValue().getProduct().getProductName());
            }
            return new SimpleStringProperty("Sản phẩm không xác định");
        });

        col_ItemQuantity.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));
        col_Price.setCellValueFactory(cellData -> new SimpleStringProperty(Others.formatPrice(cellData.getValue().getPrice())));
        col_TotalPrice.setCellValueFactory(cellData -> new SimpleStringProperty(Others.formatPrice(cellData.getValue().getTotalPrice())));
    }

    public void setOrderDetails(Order order) {
        if (order == null) return;

        lblOrderId.setText("Chi tiết hóa đơn #" + order.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lblProcessTime.setText(order.getOrderTime().format(formatter));

        String paymentStr = order.getPayment().name().equals("Card") ? "Chuyển khoản" : "Tiền mặt";
        lblPayment.setText(paymentStr);

        if (order.getStaff() != null) {
            lblStaffName.setText(order.getStaff().getName());
            lblStaffPhone.setText("N/A");
        }

        if (order.getCustomer() != null) {
            lblReceiverName.setText(order.getCustomer().getName());
            lblReceiverPhone.setText(order.getCustomer().getPhone());
        } else {
            lblReceiverName.setText("Khách vãng lai");
            lblReceiverPhone.setText("N/A");
        }

        List<OrderDetail> detailsList = OrderDetailBusiness.getDetailsByOrderId_BLL(order.getId());
        if (detailsList != null) {
            tbOrderDetail.setItems(FXCollections.observableArrayList(detailsList));

            int totalQty = 0;
            for (OrderDetail detail : detailsList) {
                totalQty += detail.getQuantity();
            }
            lblTotalItems.setText(String.valueOf(totalQty));
        }

        lblSubTotal.setText(Others.formatPrice(order.getSubTotal()));
        lblDiscount.setText("- " + Others.formatPrice(order.getDiscountAmount()));
        lblFinalTotal.setText(Others.formatPrice(order.getFinalAmount()));
    }
}