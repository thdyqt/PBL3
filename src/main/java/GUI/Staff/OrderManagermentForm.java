package GUI.Staff;

import BusinessBLL.OrderBusiness;
import EntityDTO.Order;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class OrderManagermentForm implements Initializable {

    @FXML private Button btnCancel;
    @FXML private Button btnDetail;
    @FXML private Button btnUpdate;
    @FXML private ComboBox<String> cbState;

    @FXML private TableColumn<Order, Integer> colSTT;
    @FXML private TableColumn<Order, String> colID;
    @FXML private TableColumn<Order, String> colName;
    @FXML private TableColumn<Order, String> colPhone;
    @FXML private TableColumn<Order, String> colTotal;
    @FXML private TableColumn<Order, String> colState;

    @FXML private StackPane rootPane;
    @FXML private TableView<Order> tableOrder;
    @FXML private TextField txtSearch;

    private ObservableList<Order> orderList;
    private FilteredList<Order> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Khởi tạo các tùy chọn trạng thái cho ComboBox
        cbState.setItems(FXCollections.observableArrayList(
                "Waiting_for_validation", "Processing", "Delivering", "Finished"
        ));

        // Cài đặt các cột cho bảng
        setupTable();

        // Tải dữ liệu ban đầu
        loadData();

        // Bắt sự kiện gõ phím để tìm kiếm theo thời gian thực
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            Search();
        });
    }

    private void setupTable() {
        colSTT.setCellValueFactory(cellData -> new SimpleIntegerProperty(tableOrder.getItems().indexOf(cellData.getValue()) + 1).asObject());
        colID.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));

        colName.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCustomer() != null ? cellData.getValue().getCustomer().getName() : "Khách vãng lai"
        ));

        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCustomer() != null ? cellData.getValue().getCustomer().getPhone() : "N/A"
        ));

        colTotal.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%,d đ", cellData.getValue().getTotalAmount()))
        );

// CSS cho cột
        colTotal.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-alignment: CENTER_RIGHT; -fx-text-fill: #EF4444; -fx-font-weight: bold;");
                }
            }
        });

        colState.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));

        // ĐỔI MÀU CỘT TRẠNG THÁI (Mỗi trạng thái 1 màu khác nhau)
        colState.setCellFactory(column -> {
            return new TableCell<Order, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        // Chọn màu dựa trên trạng thái của đơn hàng
                        switch (item) {
                            case "Waiting_for_validation":
                            case "Created":
                                setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: bold;"); // Màu Vàng/Cam (Cảnh báo chờ)
                                break;
                            case "Processing":
                            case "Delivering":
                                setStyle("-fx-text-fill: #3B82F6; -fx-font-weight: bold;"); // Màu Xanh dương (Đang tiến hành)
                                break;
                            case "Finished":
                                setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;"); // Màu Xanh lá (Hoàn thành)
                                break;
                            case "Cancelled":
                                setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;"); // Màu Đỏ (Đã hủy)
                                break;
                            default:
                                setStyle("-fx-text-fill: #1E293B;"); // Màu mặc định
                                break;
                        }
                    }
                }
            };
        });
    }

    private void loadData() {
        // GỌI BLL: Lấy danh sách đã được BLL lọc sẵn (chỉ Đơn Online)
        List<Order> onlineOrders = OrderBusiness.getOnlineOrders_BLL();

        if (onlineOrders != null) {
            orderList = FXCollections.observableArrayList(onlineOrders);
            filteredData = new FilteredList<>(orderList, b -> true);
            tableOrder.setItems(filteredData);
        }
    }

    @FXML
    void Search() {
        String keyword = txtSearch.getText().toLowerCase().trim();
        filteredData.setPredicate(order -> {
            if (keyword.isEmpty()) return true;
            String name = order.getCustomer() != null ? order.getCustomer().getName().toLowerCase() : "";
            String phone = order.getCustomer() != null ? order.getCustomer().getPhone().toLowerCase() : "";
            return name.contains(keyword) || phone.contains(keyword) || String.valueOf(order.getId()).contains(keyword);
        });
    }

    @FXML
    void btnCancelClick(ActionEvent event) {
        Order selectedOrder = tableOrder.getSelectionModel().getSelectedItem();

        // Chỉ kiểm tra UI (đã chọn dòng nào chưa)
        if (selectedOrder == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn đơn hàng", "Vui lòng chọn một đơn hàng để hủy!");
            return;
        }

        // Mở popup nhập lý do hủy đơn
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Hủy đơn hàng");
        dialog.setHeaderText("Xác nhận hủy đơn hàng #" + selectedOrder.getId());
        dialog.setContentText("Nhập lý do hủy đơn:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String reason = result.get().trim();

            // GỌI BLL: Đẩy toàn bộ trách nhiệm kiểm tra trạng thái và hoàn kho xuống BLL
            String msg = OrderBusiness.cancelOnlineOrder_BLL(selectedOrder, reason);

            // Hiển thị kết quả từ BLL
            if (msg.contains("thành công")) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", msg);
                loadData(); // Tải lại bảng để cập nhật trạng thái mới
            } else {
                showAlert(Alert.AlertType.ERROR, "Thất bại", msg);
            }
        }
    }

    @FXML
    void btnUpdateClick(ActionEvent event) {
        Order selectedOrder = tableOrder.getSelectionModel().getSelectedItem();
        String newState = cbState.getValue();

        // Kiểm tra UI
        if (selectedOrder == null || newState == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn 1 đơn hàng và trạng thái cần chuyển ở ComboBox!");
            return;
        }

        // GỌI BLL: Tận dụng lại hàm updateOrder_BLL cũ để kiểm tra và cập nhật trạng thái
        String msg = OrderBusiness.updateOrder_BLL(selectedOrder, newState);

        // Hiển thị thông báo
        if (msg.contains("successfully") || msg.contains("thành công")) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật trạng thái thành công!");
            loadData(); // Refresh lại bảng
        } else {
            showAlert(Alert.AlertType.ERROR, "Thất bại", msg); // Sẽ in ra lỗi nếu hàm isValidStatus báo false
        }
    }

    @FXML
    void btnDetailClick(ActionEvent event) {
        Order selectedOrder = tableOrder.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn đơn hàng", "Vui lòng chọn đơn hàng để xem chi tiết!");
            return;
        }

        try {
            // Mở form OrderDetail
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Staff/OrderDetail.fxml"));
            Parent root = loader.load();

            // Truyền dữ liệu đơn hàng sang Controller của form Chi tiết
            OrderDetailController controller = loader.getController();
            controller.setOrderData(selectedOrder);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Chi tiết đơn hàng #" + selectedOrder.getId());
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể mở form chi tiết!");
        }
    }

    // Hàm tiện ích để hiển thị thông báo
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}