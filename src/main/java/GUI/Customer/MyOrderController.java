package GUI.Customer;

import BusinessBLL.OrderBusiness;
import EntityDTO.Customer;
import EntityDTO.Order;
import Util.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MyOrderController implements Initializable {

    // ===== FXML COMPONENTS =====
    @FXML private TextField     txtSearch;
    @FXML private Label         lblTotalOrders;
    @FXML private Label         lblDoneOrders;
    @FXML private Label         lblActiveOrders;
    @FXML private Label         lblCancelOrders;
    @FXML private Label         lblTotalSpent;
    @FXML private Label         lblOrderCount;
    @FXML private VBox          orderContainer;
    @FXML private StackPane     emptyPlaceholder;

    @FXML private ToggleButton  tabAll;
    @FXML private ToggleButton  tabWaiting;
    @FXML private ToggleButton  tabPreparing;
    @FXML private ToggleButton  tabShipping;
    @FXML private ToggleButton  tabDone;
    @FXML private ToggleButton  tabCancelled;
    @FXML private ToggleGroup   filterGroup;

    // ===== DATA =====
    private Customer            currentCustomer;
    private List<Order>         allOrders;
    private List<Order>         filteredOrders;
    private String              currentFilter = "ALL";
    private StackPane           contentArea;

    // ===== INITIALIZE =====
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentCustomer = UserSession.getInstance().getCustomer();
        loadOrders();
        setupSearch();
    }

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    // ===== LOAD ORDERS =====
    private void loadOrders() {
        // Lấy tất cả online orders rồi lọc theo customer hiện tại
        allOrders = OrderBusiness.getFilteredOrders(Order.OrderType.Online)
                .stream()
                .filter(o -> o.getCustomer() != null
                        && o.getCustomer().getId() == currentCustomer.getId())
                .collect(Collectors.toList());

        updateStats();
        applyFilter();
    }

    // ===== THỐNG KÊ =====
    private void updateStats() {
        long done    = allOrders.stream().filter(o -> o.getStatus() == Order.OrderStatus.Finished).count();
        long active  = allOrders.stream().filter(o ->
                o.getStatus() == Order.OrderStatus.Waiting_for_validation ||
                        o.getStatus() == Order.OrderStatus.Processing ||
                        o.getStatus() == Order.OrderStatus.Delivering
        ).count();
        long cancel  = allOrders.stream().filter(o -> o.getStatus() == Order.OrderStatus.Cancelled).count();
        int  spent   = allOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.Finished)
                .mapToInt(Order::getFinalAmount).sum();

        lblTotalOrders.setText(String.valueOf(allOrders.size()));
        lblDoneOrders.setText(String.valueOf(done));
        lblActiveOrders.setText(String.valueOf(active));
        lblCancelOrders.setText(String.valueOf(cancel));
        lblTotalSpent.setText(formatMoney(spent) + " đ");
    }

    // ===== FILTER TABS =====
    @FXML
    private void handleFilter() {
        ToggleButton selected = (ToggleButton) filterGroup.getSelectedToggle();
        if (selected == null) {
            tabAll.setSelected(true);
            currentFilter = "ALL";
        } else {
            currentFilter = (String) selected.getUserData();
        }
        applyFilter();
    }

    private void applyFilter() {
        String keyword = txtSearch.getText().toLowerCase().trim();

        filteredOrders = allOrders.stream().filter(o -> {
            // Lọc theo tab
            boolean matchFilter = switch (currentFilter) {
                case "Chờ xác nhận"   -> o.getStatus() == Order.OrderStatus.Waiting_for_validation;
                case "Đang chuẩn bị"  -> o.getStatus() == Order.OrderStatus.Processing;
                case "Đang giao"      -> o.getStatus() == Order.OrderStatus.Delivering;
                case "Hoàn thành"     -> o.getStatus() == Order.OrderStatus.Finished;
                case "Đã hủy"         -> o.getStatus() == Order.OrderStatus.Cancelled;
                default               -> true; // ALL
            };

            // Lọc theo search
            boolean matchSearch = keyword.isEmpty()
                    || String.valueOf(o.getId()).contains(keyword);

            return matchFilter && matchSearch;
        }).collect(Collectors.toList());

        renderOrders();
    }

    // ===== RENDER CARDS =====
    private void renderOrders() {
        orderContainer.getChildren().clear();

        if (filteredOrders.isEmpty()) {
            emptyPlaceholder.setVisible(true);
            emptyPlaceholder.setManaged(true);
            orderContainer.getChildren().add(emptyPlaceholder);
            lblOrderCount.setText("0 đơn hàng");
            return;
        }

        emptyPlaceholder.setVisible(false);
        emptyPlaceholder.setManaged(false);

        for (Order order : filteredOrders) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/GUI/Customer/OrderCard.fxml")
                );
                javafx.scene.layout.VBox card = loader.load();
                OrderCardController ctrl = loader.getController();
                ctrl.setOrder(order,contentArea);
                orderContainer.getChildren().add(card);
            } catch (Exception e) {
                System.err.println("Lỗi load OrderCard: " + e.getMessage());
            }
        }

        lblOrderCount.setText(filteredOrders.size() + " đơn hàng");
    }

    // ===== SEARCH =====
    private void setupSearch() {
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
    }

    // ===== HELPER =====
    private String formatMoney(int amount) {
        return NumberFormat.getNumberInstance(new Locale("vi", "VN")).format(amount);
    }
}