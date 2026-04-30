package GUI.Staff;

import BusinessBLL.OrderBusiness;
import BusinessBLL.ProductBusiness;
import EntityDTO.Order;
import EntityDTO.Product;
import Util.Others;
import Util.UserSession;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StaffHomeController implements Initializable {

    @FXML private Label lblGreeting;
    @FXML private Label lblDate;
    @FXML private Label lblTime;
    @FXML private Label lblShift;
    @FXML private Label lblWaiting;
    @FXML private Label lblProcessing;
    @FXML private Label lblFinished;
    @FXML private Label lblRevenue;

    @FXML private Button btnPOS;
    @FXML private Button btnOnlineOrder;
    @FXML private Button btnInventory;

    @FXML private TableView<Order> tableRecentOrders;
    @FXML private TableColumn<Order, String> colOrderID;
    @FXML private TableColumn<Order, String> colCustomer;
    @FXML private TableColumn<Order, String> colTime;
    @FXML private TableColumn<Order, String> colTotal;
    @FXML private TableColumn<Order, String> colStatus;

    @FXML private ListView<String> listLowStock;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupClock();
        setupGreeting();
        setupTable();
        loadStatistics();
        loadRecentOrders();
        loadLowStock();
        setupButtons();
    }

    private void setupClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime currentTime = LocalTime.now();
            lblTime.setText(currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            lblDate.setText("Hôm nay là " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            int hour = currentTime.getHour();
            if (hour >= 6 && hour < 12) lblShift.setText("Ca Sáng");
            else if (hour >= 12 && hour < 18) lblShift.setText("Ca Chiều");
            else lblShift.setText("Ca Tối");
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void setupGreeting() {
        String staffName = "Nhân viên";
        if (UserSession.getInstance() != null && UserSession.getInstance().getName() != null) {
            staffName = UserSession.getInstance().getName();
        }
        lblGreeting.setText("Xin chào, " + staffName + "!");
    }

    private void setupTable() {
        colOrderID.setCellValueFactory(cellData -> new SimpleStringProperty("#" + cellData.getValue().getId()));

        colCustomer.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCustomer() != null ? cellData.getValue().getCustomer().getName() : "Khách vãng lai"
        ));

        colTime.setCellValueFactory(cellData -> {
            if (cellData.getValue().getOrderTime() != null) {
                return new SimpleStringProperty(cellData.getValue().getOrderTime().format(DateTimeFormatter.ofPattern("HH:mm dd/MM")));
            }
            return new SimpleStringProperty("N/A");
        });

        colTotal.setCellValueFactory(cellData -> {
            String formattedPrice = Others.formatPrice(cellData.getValue().getFinalAmount());
            return new SimpleStringProperty(formattedPrice);
        });

        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));

        colStatus.setCellFactory(column -> new TableCell<Order, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    switch (item) {
                        case "Waiting_for_validation":
                            setText("Chờ xác nhận");
                            setStyle("-fx-text-fill: #fddc05; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        case "Processing":
                            setText("Đang xử lí");
                            setStyle("-fx-text-fill: #4c7102; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        case "Delivering":
                            setText("Đang giao hàng");
                            setStyle("-fx-text-fill: #3B82F6; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        default:
                            setText(item);
                            setStyle("-fx-text-fill: #1E293B; -fx-alignment: CENTER;");
                            break;
                    }
                }
            }
        });
    }

    private void loadStatistics() {
        List<Order> onlineOrders = OrderBusiness.getFilteredOrders(Order.OrderType.Online);

        long waiting = onlineOrders.stream().filter(o -> o.getStatus() == Order.OrderStatus.Waiting_for_validation).count();
        long processing = onlineOrders.stream().filter(o -> o.getStatus() == Order.OrderStatus.Processing).count();
        long finished = onlineOrders.stream().filter(o -> o.getStatus() == Order.OrderStatus.Finished).count();

        List<Order> allOrders = OrderBusiness.getAllOrder();
        long revenue = allOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.Finished)
                .mapToInt(Order::getFinalAmount)
                .sum();

        lblWaiting.setText(String.valueOf(waiting));
        lblProcessing.setText(String.valueOf(processing));
        lblFinished.setText(String.valueOf(finished));
        lblRevenue.setText(Others.formatPrice((int) revenue));
    }

    private void loadRecentOrders() {
        List<Order> onlineOrders = BusinessBLL.OrderBusiness.getFilteredOrders(Order.OrderType.Online);

        List<Order> recentOrders = onlineOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.Waiting_for_validation ||
                        o.getStatus() == Order.OrderStatus.Processing ||
                        o.getStatus() == Order.OrderStatus.Delivering)
                .sorted(Comparator.comparing(Order::getOrderTime))
                .collect(Collectors.toList());

        tableRecentOrders.setItems(FXCollections.observableArrayList(recentOrders));
    }

    private void loadLowStock() {
        List<Product> products = ProductBusiness.getAllProducts();

        List<String> lowStockItems = products.stream()
                .filter(p -> p.getQuantity() < 10)
                .map(p -> p.getProductName() + " - Chỉ còn: " + p.getQuantity())
                .collect(Collectors.toList());

        if (lowStockItems.isEmpty()) {
            lowStockItems.add("Tất cả sản phẩm đều đủ số lượng.");
        }

        listLowStock.setItems(FXCollections.observableArrayList(lowStockItems));
    }

    private void setupButtons() {
        btnPOS.setOnAction(e -> {
            StaffDashboardForm.instance.switchForm("/GUI/Staff/POS.fxml");
            StaffDashboardForm.instance.setActiveMenu(StaffDashboardForm.instance.getBtnOrder());
        });
        btnOnlineOrder.setOnAction(e -> {
            StaffDashboardForm.instance.switchForm("/GUI/Staff/OrderOnlineManagement.fxml");
            StaffDashboardForm.instance.setActiveMenu(StaffDashboardForm.instance.getBtnOnline());
        });
        btnInventory.setOnAction(e -> {
            StaffDashboardForm.instance.switchForm("/GUI/Staff/ProductView.fxml");
            StaffDashboardForm.instance.setActiveMenu(StaffDashboardForm.instance.getBtnProduct());
        });
    }
}