package GUI.Staff;

import BusinessBLL.PromoCodeBusiness;
import EntityDTO.PromoCode;
import Util.Others;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.w3c.dom.Entity;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class PromoCodeDialogController implements Initializable {
    @FXML private AnchorPane mainPanel;
    @FXML private TextField txtCode, txtDiscountValue, txtMinOrderValue;
    @FXML private ComboBox<String> cbbDiscountType;
    @FXML private DatePicker dpValidFrom, dpValidTo;
    @FXML private TextArea txtDescription;
    @FXML private Label lblTitle, lblDesc;
    @FXML private Button btnCancel, btnSave;

    private PromoCode currentPromo = null;
    private boolean saveSuccess;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbbDiscountType.setItems(FXCollections.observableArrayList("Percent", "Amount"));

        Others.setMaxLength(txtCode, 20);
        Others.setMaxLength(txtDiscountValue, 10);
        Others.setMaxLength(txtMinOrderValue, 10);

        txtCode.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains(" ")) {
                txtCode.setText(newValue.replaceAll(" ", ""));
            }
        });

        txtDiscountValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtDiscountValue.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        txtMinOrderValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtMinOrderValue.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public boolean isSaveSuccess() {
        return saveSuccess;
    }

    public void setData(PromoCode promo) {
        this.currentPromo = promo;
        lblTitle.setText("CẬP NHẬT MÃ GIẢM GIÁ");

        txtCode.setText(promo.getCode());
        txtCode.setEditable(false);
        txtDiscountValue.setText(String.valueOf(promo.getDiscountValue()));
        txtMinOrderValue.setText(String.valueOf(promo.getMinOrderValue()));
        cbbDiscountType.setValue(String.valueOf(promo.getDiscountType()));

        if (promo.getValidFrom() != null) dpValidFrom.setValue(promo.getValidFrom().toLocalDate());
        if (promo.getValidTo() != null) dpValidTo.setValue(promo.getValidTo().toLocalDate());

        txtDescription.setText(promo.getDescription());
    }

    @FXML
    void btnSaveClick(ActionEvent event) {
        String code = txtCode.getText().trim();
        PromoCode.codeType type = PromoCode.codeType.valueOf(cbbDiscountType.getValue());
        String valueStr = txtDiscountValue.getText().trim();
        String minStr = txtMinOrderValue.getText().trim();
        String desc = txtDescription.getText().trim();

        LocalDateTime fromDate = dpValidFrom.getValue() != null ? dpValidFrom.getValue().atStartOfDay() : null;
        LocalDateTime toDate = dpValidTo.getValue() != null ? dpValidTo.getValue().atTime(23, 59, 59) : null;

        if (currentPromo != null) {
            boolean isCodeUnchanged = code.equals(currentPromo.getCode());
            boolean isTypeUnchanged = type != null && type.equals(currentPromo.getDiscountType());
            boolean isValueUnchanged = valueStr.equals(String.valueOf(currentPromo.getDiscountValue()));
            boolean isMinUnchanged = minStr.equals(String.valueOf(currentPromo.getMinOrderValue()));
            boolean isDescUnchanged = desc.equals(currentPromo.getDescription() != null ? currentPromo.getDescription() : "");

            boolean isFromUnchanged = (fromDate == null && currentPromo.getValidFrom() == null) ||
                    (fromDate != null && currentPromo.getValidFrom() != null && fromDate.toString().equals(currentPromo.getValidFrom().toString()));
            boolean isToUnchanged = (toDate == null && currentPromo.getValidTo() == null) ||
                    (toDate != null && currentPromo.getValidTo() != null && toDate.toString().equals(currentPromo.getValidTo().toString()));

            if (isCodeUnchanged && isTypeUnchanged && isValueUnchanged && isMinUnchanged && isDescUnchanged && isFromUnchanged && isToUnchanged) {
                Others.showAlert(mainPanel, "Không có thông tin nào được thay đổi!", true);
                return;
            }
        }

        if (code.isEmpty() || type == null || valueStr.isEmpty() || minStr.isEmpty()) {
            Others.showAlert(mainPanel, "Vui lòng điền đầy đủ thông tin bắt buộc!", true);
            return;
        }

        int value = Integer.parseInt(valueStr);
        int minOrder = Integer.parseInt(minStr);

        if (type == PromoCode.codeType.Percent && value > 100) {
            Others.showAlert(mainPanel, "Giảm theo phần trăm không được vượt quá 100%!", true);
            txtDiscountValue.requestFocus();
            return;
        }

        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            Others.showAlert(mainPanel, "Ngày bắt đầu không được lớn hơn ngày kết thúc!", true);
            return;
        }

        btnCancel.setDisable(true);
        btnSave.setDisable(true);
        Others.showAlert(mainPanel, "Đang kết nối máy chủ...", false);

        new Thread(() -> {
            int status = (currentPromo == null)
                    ? PromoCodeBusiness.addPromoCode(code, desc, value, type, minOrder, fromDate, toDate)
                    : PromoCodeBusiness.updatePromoCode(code, desc, value, type, minOrder, fromDate, toDate);

            javafx.application.Platform.runLater(() -> {
                btnCancel.setDisable(false);
                btnSave.setDisable(false);

                if (status == 1) {
                    if (currentPromo == null) Others.showAlert(mainPanel, "Đã thêm mã giảm giá thành công!", false);
                    else Others.showAlert(mainPanel, "Đã cập nhật mã giảm giá thành công!", false);
                    saveSuccess = true;

                    var delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2.0));
                    delay.setOnFinished(e -> ((javafx.stage.Stage) btnSave.getScene().getWindow()).close());
                    delay.play();
                } else if (status == -1) {
                    Others.showAlert(mainPanel, "Mã code này đã tồn tại, vui lòng nhập mã khác!", true);
                } else {
                    Others.showAlert(mainPanel, "Lỗi kết nối máy chủ dữ liệu!", true);
                }
            });
        }).start();
    }

    @FXML
    void btnCancelClick(ActionEvent event) {
        ((Stage) txtCode.getScene().getWindow()).close();
    }
}