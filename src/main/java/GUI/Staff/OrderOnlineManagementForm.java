package GUI.Staff;

import BusinessBLL.OrderBusiness;
import EntityDTO.Order;
import Util.Others;
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
import javafx.stage.Window;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OrderOnlineManagementForm implements Initializable {
    @FXML private Button btnCancel;
    @FXML private Button btnDetail;
    @FXML private Button btnUpdate;
    @FXML private ComboBox<String> cbState;
    @FXML private ComboBox<String> cbFilterState;

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
        cbState.setItems(FXCollections.observableArrayList(
                "Chờ xác nhận", "Đang xử lí", "Đang giao hàng", "Đã hoàn thành"
        ));

        cbFilterState.setItems(FXCollections.observableArrayList(
                "Tất cả", "Chờ xác nhận", "Đang xử lí", "Đang giao hàng", "Đã hoàn thành", "Đã hủy"
        ));
        cbFilterState.setValue("Tất cả");

        // Cài đặt các cột cho bảng
        setupTable();

        // Tải dữ liệu ban đầu
        loadData();

        //Thêm animation cho nút
        setupButtons();

        // Bắt sự kiện gõ phím để tìm kiếm theo thời gian thực
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            Search();
        });

        cbFilterState.valueProperty().addListener((observable, oldValue, newValue) -> {
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
                new SimpleStringProperty(Others.formatPrice(cellData.getValue().getFinalAmount()))
        );

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

        colState.setCellFactory(column -> {
            return new TableCell<Order, String>() {
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
                                setStyle("-fx-text-fill: #3B82F6; -fx-font-weight: bold; -fx-alignment: CENTER;"); // Màu Xanh dương (Đang tiến hành)
                                break;
                            case "Finished":
                                setText("Đã hoàn thành");
                                setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold; -fx-alignment: CENTER;"); // Màu Xanh lá (Hoàn thành)
                                break;
                            case "Cancelled":
                                setText("Đã hủy");
                                setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-alignment: CENTER;"); // Màu Đỏ (Đã hủy)
                                break;
                            default:
                                setText(item);
                                setStyle("-fx-text-fill: #1E293B; -fx-alignment: CENTER;"); // Màu mặc định
                                break;
                        }
                    }
                }
            };
        });
    }

    private void loadData() {
        List<Order> onlineOrders = OrderBusiness.getFilteredOrders(Order.OrderType.Online);

        if (onlineOrders != null) {
            orderList = FXCollections.observableArrayList(onlineOrders);
            filteredData = new FilteredList<>(orderList, b -> true);
            tableOrder.setItems(filteredData);
            Others.animateTableRows(tableOrder);
        }
    }
    private void setupButtons() {
        Others.playButtonAnimation(btnCancel);
        Others.playButtonAnimation(btnDetail);
        Others.playButtonAnimation(btnUpdate);

    }

    @FXML
    void Search() {
        String keyword = txtSearch.getText().toLowerCase().trim();
        String filterState = cbFilterState.getValue();

        filteredData.setPredicate(order -> {
            boolean matchFilter = true;
            if (filterState != null && !filterState.equals("Tất cả")) {
                String vieStatus = getVietnameseStatus(order.getStatus().name());
                if (!vieStatus.equals(filterState)) {
                    matchFilter = false;
                }
            }

            boolean matchKeyword = true;
            if (!keyword.isEmpty()) {
                String name = order.getCustomer() != null ? order.getCustomer().getName().toLowerCase() : "";
                String phone = order.getCustomer() != null ? order.getCustomer().getPhone().toLowerCase() : "";
                String id = String.valueOf(order.getId());
                String status = getVietnameseStatus(order.getStatus().name()).toLowerCase();

                matchKeyword = name.contains(keyword) || phone.contains(keyword) || id.contains(keyword) || status.contains(keyword);
            }

            return matchFilter && matchKeyword;
        });
    }

    private String getVietnameseStatus(String engStatus) {
        switch (engStatus) {
            case "Waiting_for_validation": return "Chờ xác nhận";
            case "Processing": return "Đang xử lí";
            case "Delivering": return "Đang giao hàng";
            case "Finished": return "Đã hoàn thành";
            case "Cancelled": return "Đã hủy";
            default: return engStatus;
        }
    }

    @FXML
    void btnCancelClick(ActionEvent event) {
        Order selectedOrder = tableOrder.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            Others.showAlert(rootPane, "Chưa chọn đơn hàng, vui lòng chọn đơn hàng để Hủy!", true);
            return;
        }

        if (selectedOrder.getStatus() != Order.OrderStatus.Waiting_for_validation) {
            Others.showAlert(rootPane, "Không thể hủy đơn hàng nếu đã xác nhận!", true);
            return;
        }

        Window ownerWindow = rootPane.getScene().getWindow();
        String reason = Others.showCancelReasonDialog(ownerWindow, String.valueOf(selectedOrder.getId()));

        if (reason != null && !reason.trim().isEmpty()) {
            String msg = OrderBusiness.cancelOnlineOrder(selectedOrder, reason);

            if (msg.contains("thành công")) {
                Others.showAlert(rootPane, msg, false);
                loadData();
            } else {
                Others.showAlert(rootPane, msg, true);
            }
        }
    }

    @FXML
    void btnUpdateClick(ActionEvent event) {
        Order selectedOrder = tableOrder.getSelectionModel().getSelectedItem();
        Order.OrderStatus newState = null;
        switch (cbState.getValue()){
            case "Chờ xác nhận": newState = Order.OrderStatus.Waiting_for_validation; break;
            case "Đang xử lí": newState = Order.OrderStatus.Processing; break;
            case "Đang giao hàng": newState = Order.OrderStatus.Delivering; break;
            case "Đã hoàn thành" : newState = Order.OrderStatus.Finished; break;
        }

        if (selectedOrder == null || newState == null) {
            Others.showAlert(rootPane, "Vui lòng chọn đơn hàng và trạng thái cần chuyển!", true);
            return;
        }

        String msg = OrderBusiness.updateOrder(selectedOrder, newState);

        if (msg.contains("Đã cập nhật trạng thái đơn hàng thành công.")) {
            Others.showAlert(rootPane, msg, false);
            loadData();
        } else {
            Others.showAlert(rootPane, msg, true);
        }
    }

    @FXML
    void btnDetailClick(ActionEvent event) {
        Order selectedOrder = tableOrder.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            Others.showAlert(rootPane, "Vui lòng chọn đơn hàng để xem chi tiết !", true);
            return;
        }

        try {
            // Mở form OrderDetail
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Staff/OrderOnlineDetail.fxml"));
            Parent root = loader.load();

            // Truyền dữ liệu đơn hàng sang Controller của form Chi tiết
            OrderOnlineDetailController controller = loader.getController();
            controller.setOrderData(selectedOrder);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Chi tiết đơn hàng #" + selectedOrder.getId());
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Others.showAlert(rootPane, "Lỗi hệ thống ! Không thể mở Form chi tiết", true);
        }
    }


}