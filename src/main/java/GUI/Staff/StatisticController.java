package GUI.Staff;

import BusinessBLL.OrderBusiness;
import BusinessBLL.OrderDetailBusiness;
import EntityDTO.Order;
import EntityDTO.OrderDetail;
import Util.Others;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticController implements Initializable {
    @FXML private StackPane rootPane;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private ComboBox<String> cbTimeFilter;
    @FXML private TextField txtStaffSearch;
    @FXML private Button btnFilter;
    @FXML private Button btnExport;

    @FXML private Label lblTotalRevenue;
    @FXML private Label lblTotalOrders;
    @FXML private Label lblTotalProducts;
    @FXML private Label lblCancelRate;

    @FXML private BarChart<String, Number> chartRevenue;
    @FXML private PieChart pieChartSource;

    @FXML private TableView<ProductStat> tableTopProducts;
    @FXML private TableColumn<ProductStat, Integer> colProdSTT;
    @FXML private TableColumn<ProductStat, String> colProdName;
    @FXML private TableColumn<ProductStat, String> colProdQuantity;

    @FXML private TableView<CustomerStat> tableTopCustomers;
    @FXML private TableColumn<CustomerStat, Integer> colCusSTT;
    @FXML private TableColumn<CustomerStat, String> colCusName;
    @FXML private TableColumn<CustomerStat, String> colCusTotal;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTimeFilters();
        setupTables();
        setupButtons();
        loadStatistics();
    }

    private void setupTimeFilters() {
        cbTimeFilter.setItems(FXCollections.observableArrayList(
                "Hôm nay", "Tuần này", "Tháng này", "Năm nay", "Tất cả"
        ));
        cbTimeFilter.getSelectionModel().select("Tất cả");

        cbTimeFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            LocalDate today = LocalDate.now();
            switch (newVal) {
                case "Hôm nay":
                    dpStartDate.setValue(today);
                    dpEndDate.setValue(today);
                    break;
                case "Tuần này":
                    dpStartDate.setValue(today.minusDays(today.getDayOfWeek().getValue() - 1));
                    dpEndDate.setValue(today);
                    break;
                case "Tháng này":
                    dpStartDate.setValue(today.withDayOfMonth(1));
                    dpEndDate.setValue(today);
                    break;
                case "Năm nay":
                    dpStartDate.setValue(today.withDayOfYear(1));
                    dpEndDate.setValue(today);
                    break;
                case "Tất cả":
                    dpStartDate.setValue(null);
                    dpEndDate.setValue(null);
                    break;
            }
        });

        dpStartDate.setValue(LocalDate.now().withDayOfMonth(1));
        dpEndDate.setValue(LocalDate.now());
    }

    private void setupTables() {
        colProdSTT.setCellValueFactory(data -> new SimpleIntegerProperty(tableTopProducts.getItems().indexOf(data.getValue()) + 1).asObject());
        colProdName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colProdQuantity.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getQuantity())));

        colCusSTT.setCellValueFactory(data -> new SimpleIntegerProperty(tableTopCustomers.getItems().indexOf(data.getValue()) + 1).asObject());
        colCusName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colCusTotal.setCellValueFactory(data -> new SimpleStringProperty(Others.formatPrice(data.getValue().getTotalSpent())));
    }

    private void setupButtons() {
        Others.playButtonAnimation(btnFilter);
        Others.playButtonAnimation(btnExport);

        btnFilter.setOnAction(e -> loadStatistics());

        btnExport.setOnAction(e -> exportToExcel());
    }

    private void loadStatistics() {
        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();
        String staffKeyword = txtStaffSearch.getText() != null ? txtStaffSearch.getText().trim().toLowerCase() : "";

        List<Order> allOrders = OrderBusiness.getAllOrder();

        List<Order> filteredOrders = allOrders.stream().filter(o -> {
            LocalDate orderDate = o.getOrderTime().toLocalDate();
            boolean afterStart = (startDate == null) || !orderDate.isBefore(startDate);
            boolean beforeEnd = (endDate == null) || !orderDate.isAfter(endDate);

            boolean matchStaff = true;
            if (!staffKeyword.isEmpty()) {
                if (o.getStaff() != null) {
                    String sId = String.valueOf(o.getStaff().getId());
                    String sName = o.getStaff().getName() != null ? o.getStaff().getName().toLowerCase() : "";
                    String sUser = o.getStaff().getUser() != null ? o.getStaff().getUser().toLowerCase() : "";
                    matchStaff = sId.contains(staffKeyword) || sName.contains(staffKeyword) || sUser.contains(staffKeyword);
                } else {
                    matchStaff = false;
                }
            }

            return afterStart && beforeEnd && matchStaff;
        }).collect(Collectors.toList());

        long totalRevenue = 0;
        long totalOrders = 0;
        long totalCancelled = 0;
        long totalProductsSold = 0;

        int onlineCount = 0;
        int offlineCount = 0;

        Map<LocalDate, Long> revenueByDate = new TreeMap<>();
        Map<Integer, ProductStat> productStats = new HashMap<>();
        Map<Integer, CustomerStat> customerStats = new HashMap<>();

        for (Order o : filteredOrders) {
            if (o.getStatus() == Order.OrderStatus.Cancelled) {
                totalCancelled++;
                continue;
            }

            if (o.getStatus() == Order.OrderStatus.Finished) {
                totalOrders++;
                totalRevenue += o.getFinalAmount();

                LocalDate date = o.getOrderTime().toLocalDate();
                revenueByDate.put(date, revenueByDate.getOrDefault(date, 0L) + o.getFinalAmount());

                if (o.getType() == Order.OrderType.Online) onlineCount++;
                else offlineCount++;

                if (o.getCustomer() != null && o.getCustomer().getId() > 0) {
                    int cusId = o.getCustomer().getId();
                    CustomerStat cs = customerStats.getOrDefault(cusId, new CustomerStat(cusId, o.getCustomer().getName(), 0));
                    cs.addSpent(o.getFinalAmount());
                    customerStats.put(cusId, cs);
                }

                List<OrderDetail> details = OrderDetailBusiness.getDetailsByOrderId_BLL(o.getId());
                for (OrderDetail d : details) {
                    totalProductsSold += d.getQuantity();
                    int pId = d.getProduct().getProductID();
                    ProductStat ps = productStats.getOrDefault(pId, new ProductStat(pId, d.getProduct().getProductName(), 0));
                    ps.addQuantity(d.getQuantity());
                    productStats.put(pId, ps);
                }
            }
        }

        lblTotalRevenue.setText(Others.formatPrice((int) totalRevenue));
        lblTotalOrders.setText(String.valueOf(totalOrders));
        lblTotalProducts.setText(String.valueOf(totalProductsSold));

        double cancelRate = filteredOrders.isEmpty() ? 0 : (double) totalCancelled / filteredOrders.size() * 100;
        lblCancelRate.setText(String.format("%.1f%%", cancelRate));

        updateCharts(revenueByDate, onlineCount, offlineCount);
        updateTables(productStats, customerStats);
    }

    private void updateCharts(Map<LocalDate, Long> revenueByDate, int onlineCount, int offlineCount) {
        chartRevenue.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM");

        for (Map.Entry<LocalDate, Long> entry : revenueByDate.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().format(dtf), entry.getValue()));
        }
        chartRevenue.getData().add(series);

        pieChartSource.getData().clear();
        if (onlineCount > 0 || offlineCount > 0) {
            pieChartSource.getData().add(new PieChart.Data("Tại quầy (" + offlineCount + ")", offlineCount));
            pieChartSource.getData().add(new PieChart.Data("Online (" + onlineCount + ")", onlineCount));
        }
    }

    private void updateTables(Map<Integer, ProductStat> productStats, Map<Integer, CustomerStat> customerStats) {
        List<ProductStat> topProducts = productStats.values().stream()
                .sorted((p1, p2) -> Integer.compare(p2.getQuantity(), p1.getQuantity()))
                .limit(10)
                .collect(Collectors.toList());
        tableTopProducts.setItems(FXCollections.observableArrayList(topProducts));

        List<CustomerStat> topCustomers = customerStats.values().stream()
                .sorted((c1, c2) -> Integer.compare(c2.getTotalSpent(), c1.getTotalSpent()))
                .limit(10)
                .collect(Collectors.toList());
        tableTopCustomers.setItems(FXCollections.observableArrayList(topCustomers));
    }

    public static class ProductStat {
        private int id;
        private String name;
        private int quantity;

        public ProductStat(int id, String name, int quantity) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
        }

        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public void addQuantity(int q) { this.quantity += q; }
    }

    public static class CustomerStat {
        private int id;
        private String name;
        private int totalSpent;

        public CustomerStat(int id, String name, int totalSpent) {
            this.id = id;
            this.name = name;
            this.totalSpent = totalSpent;
        }

        public String getName() { return name; }
        public int getTotalSpent() { return totalSpent; }
        public void addSpent(int amount) { this.totalSpent += amount; }
    }

    private void exportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Xuất báo cáo Thống kê");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files (*.csv)", "*.csv"));
        fileChooser.setInitialFileName("BaoCao_ThongKe_" + java.time.LocalDate.now() + ".csv");

        File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
                writer.write('\ufeff');

                writer.println("BÁO CÁO THỐNG KÊ HOẠT ĐỘNG KINH DOANH");
                writer.println("Từ ngày:," + (dpStartDate.getValue() != null ? dpStartDate.getValue() : "Bắt đầu"));
                writer.println("Đến ngày:," + (dpEndDate.getValue() != null ? dpEndDate.getValue() : "Hiện tại"));
                writer.println();

                writer.println("TỔNG QUAN:");
                writer.println("Tổng doanh thu:,\"" + lblTotalRevenue.getText() + "\"");
                writer.println("Tổng đơn hoàn thành:," + lblTotalOrders.getText());
                writer.println("Sản phẩm đã bán:," + lblTotalProducts.getText());
                writer.println("Tỉ lệ hủy đơn:," + lblCancelRate.getText());
                writer.println();

                writer.println("TOP BÁNH BÁN CHẠY NHẤT");
                writer.println("STT,Tên bánh,Số lượng đã bán");
                for (int i = 0; i < tableTopProducts.getItems().size(); i++) {
                    ProductStat p = tableTopProducts.getItems().get(i);
                    writer.println((i + 1) + ",\"" + p.getName() + "\"," + p.getQuantity());
                }
                writer.println();

                writer.println("TOP KHÁCH HÀNG CHI TIÊU NHIỀU NHẤT");
                writer.println("STT,Khách hàng,Tổng chi tiêu");
                for (int i = 0; i < tableTopCustomers.getItems().size(); i++) {
                    CustomerStat c = tableTopCustomers.getItems().get(i);
                    writer.println((i + 1) + ",\"" + c.getName() + "\",\"" + Others.formatPrice(c.getTotalSpent()) + "\"");
                }

                Others.showAlert(rootPane, "Xuất báo cáo thành công!", false);
            } catch (Exception e) {
                e.printStackTrace();
                Others.showAlert(rootPane, "Lỗi khi lưu file: " + e.getMessage(), true);
            }
        }
    }
}