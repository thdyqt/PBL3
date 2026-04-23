package GUI.Customer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class OrderSuccessDialogController {
    @FXML private Label lblOrderId;

    private boolean isViewOrderSelected = false;

    public void setOrderId(String orderId) {
        lblOrderId.setText("#" + orderId);
    }

    @FXML
    private void handleViewOrder() {
        isViewOrderSelected = true;
        closeWindow();
    }

    @FXML
    private void handleNewOrder() {
        isViewOrderSelected = false;
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) lblOrderId.getScene().getWindow()).close();
    }

    public boolean isViewOrderSelected() {
        return isViewOrderSelected;
    }
}
