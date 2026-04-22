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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private TableColumn<PromoCode, PromoCode.Type> colType;

    @FXML
    private TableColumn<PromoCode, LocalDateTime> colEnd;

    @FXML
    private TableColumn<PromoCode, Integer> colMinValue;

    @FXML
    private TableColumn<PromoCode, Integer> colSTT;

    @FXML
    private TableColumn<PromoCode, LocalDateTime> colStart;

    @FXML
    private TableColumn<PromoCode, PromoCode.CodeStatus> colStatus;

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
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
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

        colType.setCellFactory(column -> new TableCell<PromoCode, PromoCode.Type>() {
            @Override
            protected void updateItem(PromoCode.Type type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (type == PromoCode.Type.All) {
                        setText("Online & Offline");
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #8B5CF6; -fx-font-weight: bold;"); // Màu tím cho nổi bật
                    } else if (type == PromoCode.Type.Online) {
                        setText("Online");
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #0284C7; -fx-font-weight: bold;"); // Màu xanh biển
                    } else if (type == PromoCode.Type.Offline) {
                        setText("Offline");
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #D97706; -fx-font-weight: bold;"); // Màu cam
                    }
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

        colStart.setCellFactory(column -> new TableCell<PromoCode, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        colEnd.setCellFactory(column -> new TableCell<PromoCode, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        colStatus.setCellFactory(column -> new TableCell<PromoCode, PromoCode.CodeStatus>() {
            @Override
            protected void updateItem(PromoCode.CodeStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label lblStatus = new Label();
                    lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 4 12; -fx-background-radius: 12;");

                    if (status == PromoCode.CodeStatus.Active) {
                        lblStatus.setText("Đang chạy");
                        lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #10B981;"); // Xanh lá
                    } else if (status == PromoCode.CodeStatus.Paused) {
                        lblStatus.setText("Tạm ngưng");
                        lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #F59E0B;"); // Vàng cam
                    } else if (status == PromoCode.CodeStatus.Upcoming) {
                        lblStatus.setText("Sắp diễn ra");
                        lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #3B82F6;"); // Xanh dương
                    } else if (status == PromoCode.CodeStatus.Expired) {
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
                if ("Active".equalsIgnoreCase(String.valueOf(newValue.getStatus()))) {
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
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(promoCode -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();

                String startDateStr = "";
                if (promoCode.getValidFrom() != null) {
                    startDateStr = promoCode.getValidFrom().format(dtf);
                }

                String endDateStr = "";
                if (promoCode.getValidTo() != null) {
                    endDateStr = promoCode.getValidTo().format(dtf);
                }

                String statusVN = "";
                if (promoCode.getStatus() != null) {
                    String rawStatus = promoCode.getStatus().name();
                    if ("Active".equalsIgnoreCase(rawStatus)) statusVN = "đang chạy";
                    else if ("Paused".equalsIgnoreCase(rawStatus)) statusVN = "tạm ngưng";
                    else if ("Upcoming".equalsIgnoreCase(rawStatus)) statusVN = "sắp diễn ra";
                    else if ("Expired".equalsIgnoreCase(rawStatus)) statusVN = "đã hết hạn";
                }

                String typeVN = "";
                if (promoCode.getType() != null) {
                    if (promoCode.getType() == PromoCode.Type.All) typeVN = "online & offline"; // Hoặc có thể gán là "cả 2"
                    else if (promoCode.getType() == PromoCode.Type.Online) typeVN = "online";
                    else if (promoCode.getType() == PromoCode.Type.Offline) typeVN = "offline";
                }

                return (promoCode.getCode() != null && promoCode.getCode().toLowerCase().contains(searchKeyword)) ||
                        (promoCode.getDescription() != null && promoCode.getDescription().toLowerCase().contains(searchKeyword)) ||
                        statusVN.contains(searchKeyword) ||
                        typeVN.contains(searchKeyword) ||
                        String.valueOf(promoCode.getMinOrderValue()).contains(searchKeyword) ||
                        startDateStr.contains(searchKeyword) ||
                        endDateStr.contains(searchKeyword);
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

        String currentStatus = String.valueOf(selected.getStatus());

        if ("Expired".equalsIgnoreCase(currentStatus)) {
            Others.showAlert(mainPane, "Mã này đã hết hạn, không thể tiếp tục!", true);
            return;
        }

        String newStatus = "Active".equalsIgnoreCase(currentStatus) ? "Paused" : "Active";
        String actionName = "Active".equals(newStatus) ? "Kích hoạt" : "Tạm ngưng";

        if (Others.showCustomConfirm("Xác nhận", "Bạn có chắc muốn " + actionName + " mã giảm giá: " + selected.getCode() + "?", "Đồng ý", "Hủy")) {

            if ("Upcoming".equalsIgnoreCase(currentStatus) && "Active".equals(newStatus)) {
                LocalDateTime now = LocalDateTime.now();
                if (PromoCodeBusiness.updatePromoStatusAndStartDate(selected.getCode(), PromoCode.CodeStatus.Active, now)) {
                    Others.showAlert(mainPane, "Đã kích hoạt và dời ngày bắt đầu về hôm nay!", false);
                    loadTable();
                } else {
                    Others.showAlert(mainPane, "Có lỗi xảy ra khi cập nhật!", true);
                }
            }
            else {
                if (PromoCodeBusiness.updatePromoStatus(selected.getCode(), PromoCode.CodeStatus.valueOf(newStatus))) {
                    Others.showAlert(mainPane, "Đã " + actionName + " thành công!", false);
                    loadTable();
                } else {
                    Others.showAlert(mainPane, "Có lỗi xảy ra!", true);
                }
            }
        }
    }
}