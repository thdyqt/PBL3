package GUI.Customer;

import Util.Others;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GuestDeliveryDialogController implements Initializable {
    @FXML private AnchorPane mainPane;
    @FXML private TextField txtName;
    @FXML private TextField txtPhone;
    @FXML private TextField txtAddress;

    private boolean saveSuccess = false;
    private String guestName = "";
    private String guestPhone = "";
    private String guestAddress = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Others.setMaxLength(txtPhone, 10);
        Others.setMaxLength(txtName, 100);
        Others.setMaxLength(txtAddress, 255);

        txtPhone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtPhone.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[\\p{L} .'-]*$")) {
                txtName.setText(newValue.replaceAll("[^\\p{L} .'-]", ""));
            }
        });
    }

    public void setData(String name, String phone, String address) {
        txtName.setText(name);
        txtPhone.setText(phone);
        txtAddress.setText(address);
    }

    @FXML
    void btnSaveClick(ActionEvent event) {
        String name = txtName.getText() == null ? "" : txtName.getText().trim();
        String phone = txtPhone.getText() == null ? "" : txtPhone.getText().trim();
        String address = txtAddress.getText() == null ? "" : txtAddress.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Others.showAlert(mainPane, "Vui lòng nhập đầy đủ Tên, SĐT và Địa chỉ!", true);
            return;
        }

        if (!phone.matches("^0[0-9]{9}$")) {
            Others.showAlert(mainPane, "Số điện thoại không hợp lệ!", true);
            txtPhone.requestFocus();
            return;
        }

        this.guestName = Others.standardizeName(name);
        this.guestPhone = phone;
        this.guestAddress = address;
        this.saveSuccess = true;

        closeWindow();
    }

    @FXML
    void btnCancelClick(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) txtName.getScene().getWindow()).close();
    }

    public boolean isSaveSuccess() { return saveSuccess; }
    public String getGuestName() { return guestName; }
    public String getGuestPhone() { return guestPhone; }
    public String getGuestAddress() { return guestAddress; }
}