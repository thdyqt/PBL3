package GUI;

import Data.CustomerData;
import Entity.Customer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;

public class CustomerManagementForm {

    @FXML
    private Button btnSort;

    @FXML
    private Button btnAdd;

    @FXML
    private ComboBox<String> cbbSort;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<Customer> tableCustomer;

    // Khai báo các cột
    @FXML
    private TableColumn<Customer, Integer> colId;

    @FXML
    private TableColumn<Customer, String> colName;

    @FXML
    private TableColumn<Customer, String> colPhone;

    @FXML
    private TableColumn<Customer, Integer> colPoint;

    @FXML
    private TableColumn<Customer, Customer.rank> colRank;

    // Danh sách gốc chứa toàn bộ dữ liệu
    private ObservableList<Customer> customerList;

    // Danh sách đã được lọc dùng để hiển thị lên bảng
    private FilteredList<Customer> filteredData;

    @FXML
    public void initialize() {
        // 1. Ánh xạ dữ liệu cho các cột
        colId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        colPoint.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPoint()));

        // 2. Cấu hình ComboBox sắp xếp
        ObservableList<String> sortOptions = FXCollections.observableArrayList(
                "Tên: A - Z",
                "Tên: Z - A",
                "Điểm: Cao - Thấp",
                "Điểm: Thấp - Cao",
                "Mã Khách: Mới nhất"
        );
        cbbSort.setItems(sortOptions);
        cbbSort.getSelectionModel().selectFirst();

        // 3. Cấu hình cột hiển thị Hạng
        setupRankColumn();

        // 4. Tải dữ liệu và cài đặt chức năng tìm kiếm
        loadData();
    }

    private void loadData() {
        List<Customer> listCustomers = CustomerData.getAllCustomers();
        customerList = FXCollections.observableArrayList(listCustomers);

        // Bọc danh sách gốc vào một FilteredList (mặc định cho hiển thị tất cả)
        filteredData = new FilteredList<>(customerList, b -> true);

        // LẮNG NGHE SỰ KIỆN GÕ PHÍM TRONG Ô TÌM KIẾM
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                // Nếu ô tìm kiếm trống, hiển thị toàn bộ khách hàng
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Chuyển từ khóa về chữ thường để tìm kiếm không phân biệt hoa/thường
                String lowerCaseFilter = newValue.toLowerCase();

                // Kiểm tra xem Tên, Số điện thoại hoặc Mã khách có chứa từ khóa không
                if (customer.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Khớp tên
                } else if (customer.getPhone().contains(lowerCaseFilter)) {
                    return true; // Khớp SĐT
                } else if (String.valueOf(customer.getId()).contains(lowerCaseFilter)) {
                    return true; // Khớp mã ID
                }

                return false; // Không khớp tiêu chí nào thì ẩn dòng này đi
            });
        });

        // GÁN FILTERED_LIST VÀO BẢNG THAY VÌ DANH SÁCH GỐC
        tableCustomer.setItems(filteredData);
    }

    private void setupRankColumn() {
        colRank.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCustomer_rank())
        );

        colRank.setCellFactory(column -> {
            return new TableCell<Customer, Customer.rank>() {
                @Override
                protected void updateItem(Customer.rank item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Label badge = new Label();
                        badge.setStyle("-fx-padding: 3 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 12px;");

                        switch (item) {
                            case Bronze:
                                badge.setText("Đồng");
                                badge.setStyle(badge.getStyle() + "-fx-background-color: #FFEDD5; -fx-text-fill: #9A3412;");
                                break;
                            case Silver:
                                badge.setText("Bạc");
                                badge.setStyle(badge.getStyle() + "-fx-background-color: #F1F5F9; -fx-text-fill: #475569;");
                                break;
                            case Gold:
                                badge.setText("Vàng");
                                badge.setStyle(badge.getStyle() + "-fx-background-color: #FEF08A; -fx-text-fill: #854D0E;");
                                break;
                            case Platinum:
                                badge.setText("Bạch Kim");
                                badge.setStyle(badge.getStyle() + "-fx-background-color: #CCFBF1; -fx-text-fill: #115E59;");
                                break;
                            case Diamond:
                                badge.setText("Kim Cương");
                                badge.setStyle(badge.getStyle() + "-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF;");
                                break;
                        }

                        setGraphic(badge);
                        setText(null);
                    }
                }
            };
        });
    }

    @FXML
    void Search(ActionEvent event) {
        // Bỏ trống hàm này vì chúng ta đã dùng Listener real-time ở trên.
        // Giữ lại hàm này để file FXML không báo lỗi khi bạn nhấn Enter trong ô Text.
    }

    @FXML
    void btnAddClick(ActionEvent event) {
        try {
            // Tải file giao diện của cửa sổ Thêm Khách Hàng
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/CustomerDialog.fxml"));
            Parent root = loader.load();

            // Tạo một Stage (cửa sổ) mới
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Thêm khách hàng mới");
            dialogStage.setScene(new Scene(root));

            // Thiết lập chế độ Modal: Bắt buộc người dùng phải đóng cửa sổ này thì mới thao tác được với cửa sổ chính
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // Ngăn chặn việc thay đổi kích thước cửa sổ
            dialogStage.setResizable(false);

            // Hiển thị cửa sổ và chờ cho đến khi nó bị đóng
            dialogStage.showAndWait();

            // Ngay sau khi cửa sổ thêm đóng lại, ta gọi lại hàm loadData()
            // để bảng tự động cập nhật nếu có khách hàng mới vừa được thêm vào Database
            loadData();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Không thể mở cửa sổ CustomerDialog.fxml. Hãy kiểm tra lại đường dẫn!");
        }
    }

    @FXML
    void btnSortClick(ActionEvent event) {
        if (customerList == null || customerList.isEmpty()) {
            return;
        }

        String selectedOption = cbbSort.getValue();
        if (selectedOption == null) return;

        Comparator<Customer> comparator = null;

        switch (selectedOption) {
            case "Tên: A - Z":
                comparator = Comparator.comparing(Customer::getName);
                break;
            case "Tên: Z - A":
                comparator = Comparator.comparing(Customer::getName).reversed();
                break;
            case "Điểm: Cao - Thấp":
                comparator = Comparator.comparingInt(Customer::getPoint).reversed();
                break;
            case "Điểm: Thấp - Cao":
                comparator = Comparator.comparingInt(Customer::getPoint);
                break;
            case "Mã Khách: Mới nhất":
                comparator = Comparator.comparingInt(Customer::getId).reversed();
                break;
        }

        if (comparator != null) {
            // Sắp xếp trên danh sách gốc. FilteredList bọc bên ngoài sẽ tự động nhận diện
            // sự thay đổi vị trí này và cập nhật giao diện ngay lập tức.
            FXCollections.sort(customerList, comparator);
        }
    }
}