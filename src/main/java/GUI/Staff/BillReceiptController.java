package GUI.Staff;

import EntityDTO.Order;
import EntityDTO.OrderDetail;
import Util.Others;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillReceiptController {
    @FXML
    private Label lblCashierName;

    @FXML
    private Label lblCustomerName;

    @FXML
    private Label lblCustomerPhone;

    @FXML
    private Label lblCustomerPoint;

    @FXML
    private Label lblCustomerRank;

    @FXML
    private Label lblTime;

    @FXML
    private Label lblDiscount;

    @FXML
    private Label lblItemTotal;

    @FXML
    private Label lblOrderID;

    @FXML
    private Label lblPlusPoint;

    @FXML
    private Label lblSubTotal;

    @FXML
    private Label lblTotal;

    @FXML
    private VBox vboxContent;

    public void setData(Order order) {
        lblCashierName.setText("Nhân viên: " + order.getStaff().getName());

        if (order.getCustomer() != null) {
            lblCustomerName.setText("Khách hàng: " + order.getCustomer().getName());
            lblCustomerPoint.setText("Điểm: " + order.getCustomer().getPoint() + " điểm");
            lblCustomerRank.setText("Hạng thành viên: " + order.getCustomer().getCustomerRank());
            lblCustomerPhone.setText("SĐT khách hàng: " + order.getCustomer().getPhone());
        } else {
            lblCustomerName.setText("Tên Khách hàng: Khách vãng lai");
            if (lblCustomerPoint != null) lblCustomerPoint.setText("Điểm: 0");
            if (lblCustomerRank != null) lblCustomerRank.setText("Hạng thành viên: Không có");
            if (lblCustomerPhone != null) lblCustomerPhone.setText("Số điện thoại khách hàng: Không có");
        }

        lblOrderID.setText("Mã Hóa đơn: #" + order.getId());

        if (order.getOrderTime() != null) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            lblTime.setText("Thời gian in đơn: " + order.getOrderTime().format(timeFormatter));
        }

        List<OrderDetail> details = BusinessBLL.OrderDetailBusiness.getDetailsByOrderId_BLL(order.getId());

        vboxContent.getChildren().clear();
        int totalItemsCount = 0;

        GridPane itemGrid = new GridPane();
        itemGrid.setHgap(15);
        itemGrid.setVgap(8);
        itemGrid.setPadding(new javafx.geometry.Insets(0, 0, 0, 5));

        javafx.scene.layout.ColumnConstraints colName = new javafx.scene.layout.ColumnConstraints();
        colName.setHgrow(javafx.scene.layout.Priority.ALWAYS);

        javafx.scene.layout.ColumnConstraints colQty = new javafx.scene.layout.ColumnConstraints();
        colQty.setHalignment(javafx.geometry.HPos.RIGHT);

        javafx.scene.layout.ColumnConstraints colPrice = new javafx.scene.layout.ColumnConstraints();
        colPrice.setHalignment(javafx.geometry.HPos.RIGHT);

        javafx.scene.layout.ColumnConstraints colTotal = new javafx.scene.layout.ColumnConstraints();
        colTotal.setHalignment(javafx.geometry.HPos.RIGHT);

        itemGrid.getColumnConstraints().addAll(colName, colQty, colPrice, colTotal);

        int rowIndex = 0;

        for (OrderDetail item : details){
            Label labelName = new Label(item.getProduct().getProductName());
            labelName.setStyle("-fx-text-fill: #334155;");

            Label labelPrice_Singular = new Label(Others.formatPrice(item.getPrice()));
            labelPrice_Singular.setStyle("-fx-text-fill: #94A3B8;");

            Label labelQuanity = new Label("x" + item.getQuantity());
            labelQuanity.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");

            Label labelItemTotal = new Label(Others.formatPrice(item.getTotalPrice()));
            labelItemTotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #0F172A;");

            GridPane.setHalignment(labelItemTotal, javafx.geometry.HPos.RIGHT);

            itemGrid.add(labelName, 0, rowIndex);
            itemGrid.add(labelQuanity, 1, rowIndex);
            itemGrid.add(labelPrice_Singular, 2, rowIndex);
            itemGrid.add(labelItemTotal, 3, rowIndex);

            totalItemsCount += item.getQuantity();
            rowIndex++;
        }

        vboxContent.getChildren().add(itemGrid);

        if (lblItemTotal != null) lblItemTotal.setText(String.valueOf(totalItemsCount));
        if (lblSubTotal != null) lblSubTotal.setText(Others.formatPrice(order.getSubTotal()));
        if (lblDiscount != null) lblDiscount.setText("- " + Others.formatPrice(order.getDiscountAmount()));
        if (lblTotal != null) lblTotal.setText(Others.formatPrice(order.getFinalAmount()));

        if (lblPlusPoint != null) {
            if (order.getCustomer() != null) {
                int earnedPoints = order.getFinalAmount() / 1000;
                lblPlusPoint.setText("+" + earnedPoints + " điểm");
            } else {
                lblPlusPoint.setText("+0 điểm");
            }
        }
    }
}