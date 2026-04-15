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
    private Label labelDate;

    @FXML
    private Label labelOrderID;

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
        labelCashierID.setText("Cashier ID: " + String.valueOf(order.getStaff().getId()));

        if (order.getCustomer() != null) {
            labelCustomerID.setText("Customer ID: " + String.valueOf(order.getCustomer().getId()));
        } else {
            labelCustomerID.setText("Khách vãng lai");
        }

        labelOrderID.setText("Order ID: #" + String.valueOf(order.getId()));
        labelDate.setText(String.valueOf(order.getProcess_time()));

        List<OrderDetail> details = BusinessBLL.OrderDetailBusiness.getDetailsByOrderId_BLL(order.getId());

        vboxContent.getChildren().clear();
        int total = 0;

        for (OrderDetail item : details){
            GridPane row = new GridPane();
            row.setHgap(15);

            Label labelName = new Label(item.getProduct().getProductName());
            Label labelQuanity = new Label("x" + item.getQuantity());
            Label labelItemTotal = new Label(formatter.format(item.getTotalPrice()) + "đ");

            row.add(labelName, 0, 0);
            row.add(labelQuanity, 1, 0);
            row.add(labelItemTotal, 2, 0);

            vboxContent.getChildren().add(row);

            total += item.getTotalPrice();
        }

        if (labelTotal != null) {
            labelTotal.setText("Tổng tiền: " + formatter.format(total) + "đ");
        }
    }
}