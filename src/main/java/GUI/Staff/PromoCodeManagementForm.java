package GUI.Staff;

import BusinessBLL.PromoCodeBusiness;
import EntityDTO.PromoCode;
import Util.Others;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class PromoCodeManagementForm implements Initializable {
    @FXML
    private Button btnAdd;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnToggleStatus;

    @FXML
    private TableColumn<PromoCode, String> colCode;

    @FXML
    private TableColumn<PromoCode, String> colDesc;

    @FXML
    private TableColumn<PromoCode, Date> colEnd;

    @FXML
    private TableColumn<PromoCode, Integer> colMinValue;

    @FXML
    private TableColumn<PromoCode, Integer> colSTT;

    @FXML
    private TableColumn<PromoCode, Date> colStart;

    @FXML
    private TableColumn<PromoCode, String> colStatus;

    @FXML
    private BorderPane mainPane;

    @FXML
    private TableView<PromoCode> tblPromoCode;

    @FXML
    private TextField txtSearch;

    private ObservableList<PromoCode> masterData = FXCollections.observableArrayList();
    private FilteredList<PromoCode> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableStyles();
        loadTable();
        setupSearch();

        tblPromoCode.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                PromoCode selectedCode = tblPromoCode.getSelectionModel().getSelectedItem();
                if (selectedCode != null) {
                    openPromoCodeDialog(selectedCode);
                }
            }
        });
    }

    private void setupTableStyles() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colMinValue.setCellValueFactory(new PropertyValueFactory<>("minOrderValue"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("validFrom"));
        colEnd.setCellValueFactory(new PropertyValueFactory<>("validTo"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colSTT.setCellFactory(column -> new TableCell<PromoCode, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        colCode.setCellFactory(column -> new TableCell<PromoCode, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    // Lệnh này ép chữ nằm chính giữa theo chiều dọc
                    setStyle("-fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 10; -fx-font-weight: bold;");
                }
            }
        });

        colDesc.setCellFactory(column -> new TableCell<PromoCode, String>() {
            private final javafx.scene.text.Text text = new javafx.scene.text.Text();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    text.setText(item);
                    text.wrappingWidthProperty().bind(column.widthProperty().subtract(20));
                    text.setStyle("-fx-fill: #334155; -fx-font-size: 14px;");
                    VBox vbox = new javafx.scene.layout.VBox(text);
                    vbox.setAlignment(Pos.CENTER_LEFT);
                    vbox.setPadding(new Insets(0, 10, 0, 10)); // Top, Right, Bottom, Left

                    setGraphic(vbox);
                    setText(null);
                }
            }
        });

        colMinValue.setCellFactory(column -> new TableCell<PromoCode, Integer>() {
            @Override
            protected void updateItem(Integer price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,d đ", price));
                    setStyle("-fx-alignment: CENTER_RIGHT; -fx-padding: 0 15 0 0;");
                }
            }
        });

        colStart.setCellFactory(column -> new TableCell<PromoCode, Date>() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(sdf.format(item));
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        colEnd.setCellFactory(column -> new TableCell<PromoCode, Date>() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(sdf.format(item));
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        colStatus.setCellFactory(column -> new TableCell<PromoCode, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label lblStatus = new Label();
                    lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 4 12; -fx-background-radius: 12;");

                    if ("Active".equalsIgnoreCase(status)) {
                        lblStatus.setText("Đang chạy");
                        lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #10B981;"); // Xanh lá
                    } else if ("Paused".equalsIgnoreCase(status)) {
                        lblStatus.setText("Tạm ngưng");
                        lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #F59E0B;"); // Vàng cam
                    } else if ("Upcoming".equalsIgnoreCase(status)) {
                        lblStatus.setText("Sắp diễn ra");
                        lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #3B82F6;"); // Xanh dương
                    } else if ("Expired".equalsIgnoreCase(status)) {
                        lblStatus.setText("Đã hết hạn");
                        lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #EF4444;"); // Đỏ
                    }

                    setGraphic(lblStatus);
                    setText(null);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        tblPromoCode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if ("Active".equalsIgnoreCase(newValue.getStatus())) {
                    btnToggleStatus.setText("🚫 Tạm ngưng");
                    btnToggleStatus.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-weight: bold;");
                } else {
                    btnToggleStatus.setText("✅ Tiếp tục");
                    btnToggleStatus.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-weight: bold;");
                }
            }
        });
    }

    private void loadTable() {
        List<PromoCode> listFromDB = PromoCodeBusiness.getAllPromoCodes();
        masterData.setAll(listFromDB);
        filteredData = new FilteredList<>(masterData, b -> true);
        SortedList<PromoCode> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblPromoCode.comparatorProperty());
        tblPromoCode.setItems(sortedData);
        Others.animateTableRows(tblPromoCode);
    }

    private void setupSearch() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(promoCode -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();

                String startDateStr = "";
                if (promoCode.getValidFrom() != null) {
                    startDateStr = sdf.format(promoCode.getValidFrom());
                }

                String endDateStr = "";
                if (promoCode.getValidTo() != null) {
                    endDateStr = sdf.format(promoCode.getValidTo());
                }

                String statusVN = "";
                String rawStatus = promoCode.getStatus();
                if (rawStatus != null) {
                    if ("Active".equalsIgnoreCase(rawStatus)) statusVN = "đang chạy";
                    else if ("Paused".equalsIgnoreCase(rawStatus)) statusVN = "tạm ngưng";
                    else if ("Upcoming".equalsIgnoreCase(rawStatus)) statusVN = "sắp diễn ra";
                    else if ("Expired".equalsIgnoreCase(rawStatus)) statusVN = "đã hết hạn";
                }

                if (promoCode.getCode().toLowerCase().contains(searchKeyword) ||
                        (promoCode.getDescription() != null && promoCode.getDescription().toLowerCase().contains(searchKeyword)) ||
                        statusVN.contains(searchKeyword) ||
                        String.valueOf(promoCode.getMinOrderValue()).contains(searchKeyword) ||
                        startDateStr.contains(searchKeyword) ||
                        endDateStr.contains(searchKeyword)) {
                    return true;
                }

                return false;
            });
        });
    }

    private void openPromoCodeDialog(PromoCode promo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Staff/PromoCodeDialog.fxml"));
            Parent root = loader.load();
            PromoCodeDialogController controller = loader.getController();

            if (promo != null) {
                controller.setData(promo);
            }

            Stage stage = new Stage();
            stage.setTitle(promo == null ? "Thêm Mã Giảm Giá Mới" : "Cập Nhật Mã Giảm Giá");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controller.isSaveSuccess()) {
                loadTable();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Others.showAlert(mainPane, "Lỗi khi mở hộp thoại!", true);
        }
    }

    @FXML
    void btnAddClick(ActionEvent event) {
        openPromoCodeDialog(null);
    }

    @FXML
    void btnEditClick(ActionEvent event) {
        PromoCode selected = tblPromoCode.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Others.showAlert(mainPane, "Vui lòng chọn một mã giảm giá để chỉnh sửa!", true);
            return;
        }
        openPromoCodeDialog(selected);
    }

    @FXML
    void btnToggleStatusClick(ActionEvent event) {
        PromoCode selected = tblPromoCode.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Others.showAlert(mainPane, "Vui lòng chọn một mã giảm giá để đổi trạng thái!", true);
            return;
        }

        String currentStatus = selected.getStatus();

        if ("Expired".equalsIgnoreCase(currentStatus)) {
            Others.showAlert(mainPane, "Mã này đã hết hạn, không thể tiếp tục!", true);
            return;
        }

        String newStatus = "Active".equalsIgnoreCase(currentStatus) ? "Paused" : "Active";
        String actionName = "Active".equals(newStatus) ? "Kích hoạt" : "Tạm ngưng";

        if (Others.showCustomConfirm("Xác nhận", "Bạn có chắc muốn " + actionName + " mã giảm giá: " + selected.getCode() + "?", "Đồng ý", "Hủy")) {
            if (PromoCodeBusiness.updatePromoStatus(selected.getCode(), newStatus)) {
                Others.showAlert(mainPane, "Đã " + actionName + " thành công!", false);
                loadTable();
            } else {
                Others.showAlert(mainPane, "Lỗi khi cập nhật trạng thái!", true);
            }
        }
    }
}