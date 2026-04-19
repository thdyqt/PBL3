package GUI.Staff;

import EntityDTO.Order;
import EntityDTO.OrderDetail;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class BillReceiptController {

    @FXML
    private Label labelCashierID;

    @FXML
    private Label labelCustomerID;

    @FXML
    private Label labelCustomerPhone;

    @FXML
    private Label labelCustomerPoint;

    @FXML
    private Label labelCustomerRank;

    @FXML
    private Label labelDate;

    @FXML
    private Label labelDiscount;

    @FXML
    private Label labelItemTotal;

    @FXML
    private Label labelOrderID;

    @FXML
    private Label labelPlusPoint;

    @FXML
    private Label labelSubTotal;

    @FXML
    private Label labelTotal;

    @FXML
    private VBox vboxContent;

    private final DecimalFormat formatter;

    public BillReceiptController() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter = new DecimalFormat("#,###", symbols);
    }

    public void setData(Order order){
        labelCashierID.setText("Tên nhân viên: " + String.valueOf(order.getStaff().getName()));

        if (order.getCustomer() != null) {
            labelCustomerID.setText("Tên khách hàng: " + String.valueOf(order.getCustomer().getName()));
            labelCustomerPoint.setText("Tổng điểm thành viên: " + String.valueOf(order.getCustomer().getPoint()));
            labelCustomerRank.setText("Bậc khách hàng: " + String.valueOf(order.getCustomer().getCustomer_rank()));
            labelCustomerPhone.setText("Số điện thoại của khách hàng: " + String.valueOf(order.getCustomer().getPhone()));
        } else {
            labelCustomerID.setText("Khách vãng lai");
            if (labelCustomerPoint != null) labelCustomerPoint.setText("");
            if (labelCustomerRank != null) labelCustomerRank.setText("");
            if (labelCustomerPhone != null) labelCustomerPhone.setText("");
        }

        labelOrderID.setText("ID hóa đơn: #" + String.valueOf(order.getId()));
        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        labelDate.setText("Thời gian thực hiện hóa đơn: " + String.valueOf(order.getProcess_time()));

        List<OrderDetail> details = BusinessBLL.OrderDetailBusiness.getDetailsByOrderId_BLL(order.getId());

        vboxContent.getChildren().clear();
        int totalItemsCount = 0;

        GridPane itemGrid = new GridPane();
        itemGrid.setHgap(20);
        itemGrid.setVgap(5);

        int rowIndex = 0;

        for (OrderDetail item : details){
            Label labelName = new Label(item.getProduct().getProductName());
            Label labelPrice_Singular = new Label(formatter.format(item.getPrice()) + "đ");
            Label labelQuanity = new Label("x" + item.getQuantity());
            Label labelItemTotal = new Label(formatter.format(item.getTotalPrice()) + "đ");

            GridPane.setHalignment(labelItemTotal, javafx.geometry.HPos.RIGHT);

            itemGrid.add(labelName, 0, rowIndex);
            itemGrid.add(labelQuanity, 1, rowIndex);
            itemGrid.add(labelPrice_Singular, 2, rowIndex);
            itemGrid.add(labelItemTotal, 3, rowIndex);

            totalItemsCount += item.getQuantity();
            rowIndex++;
        }

        vboxContent.getChildren().add(itemGrid);

        if (labelItemTotal != null) {
            labelItemTotal.setText(String.valueOf(totalItemsCount));
        }

        if (labelSubTotal != null) {
            labelSubTotal.setText(formatter.format(order.getSubTotal()) + "đ");
        }

        if (labelDiscount != null) {
            labelDiscount.setText(formatter.format(order.getDiscountAmount()) + "đ");
        }

        if (labelTotal != null) {
            labelTotal.setText(formatter.format(order.getFinalAmount()) + "đ");
        }
    }
}