package GUI.Customer;

import EntityDTO.Customer;
import GUI.CustomerDialogController;
import Util.Others;
import Util.UserSession;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerDashboardForm implements Initializable {

    @FXML
    private Button btnCart;

    @FXML
    private Button btnExit;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnOrders;

    @FXML
    private Button btnProducts;

    @FXML
    private StackPane contentArea;

    @FXML
    private Label lblName;

    @FXML
    private Label lblPoint;

    @FXML
    private Label lblProfile;

    @FXML
    private Label lblTime;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private MenuButton menuAcc;

    @FXML
    private CustomMenuItem menuEditAcc;

    @FXML
    private CustomMenuItem menuInfo;

    private Button[] menuButtons;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainBorderPane.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(500), mainBorderPane);
        fade.setToValue(1);
        fade.play();

        menuButtons = new Button[]{btnHome, btnProducts, btnCart, btnOrders};

        loadUserProfile();

        btnProductsClick(null);

        // Sự kiện click cho Avatar (Xem và Sửa tài khoản khách hàng)
        menuInfo.setOnAction(event -> openProfileDialog(true));
        menuEditAcc.setOnAction(event -> openProfileDialog(false));

        loadUserProfile();
        Others.startClock(lblTime);
        btnHomeClick(null);
    }

    private void loadUserProfile() {
        String fullName = UserSession.getInstance().getName();
        String username = UserSession.getInstance().getUsername();
        int point = UserSession.getInstance().getPoint();

        if (fullName != null && !fullName.trim().isEmpty()) {
            lblName.setText(fullName + " (" + username + ")");

            String[] parts = fullName.trim().split("\\s+");
            String firstName = parts[parts.length - 1];
            String initial = String.valueOf(firstName.charAt(0)).toUpperCase();

            lblProfile.setText(initial);
        }

        lblPoint.setText("Điểm tích lũy: " + point);
    }

    private void setActiveMenu(Button activeButton) {
        if (menuButtons == null) return;
        for (Button btn : menuButtons) {
            if (btn != null) btn.getStyleClass().remove("active-menu");
        }
        if (activeButton != null) activeButton.getStyleClass().add("active-menu");
    }

    private void switchForm(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Node node = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(node);
        } catch (Exception e) {
            System.out.println("Lỗi khi tải trang: " + fxmlFileName);
            e.printStackTrace();
        }
    }

    @FXML
    void btnCartClick(ActionEvent event) {

    }

    @FXML
    void btnExitClick(ActionEvent event) {
        boolean isConfirm = Util.Others.showCustomConfirm(
                "Đăng xuất",
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống không?",
                "Đăng xuất", "Hủy bỏ"
        );

        if (isConfirm) {
            try {
                UserSession.getInstance().clearSession();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
                mainBorderPane.getScene().setRoot(loader.load());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnHomeClick(ActionEvent event) {

    }

    @FXML
    void btnOrdersClick(ActionEvent event) {

    }

    @FXML
    void btnProductsClick(ActionEvent event) {

    }

    private void openProfileDialog(boolean isViewOnly) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerDialog.fxml"));
            Parent root = loader.load();

            CustomerDialogController controller = loader.getController();

            UserSession session = UserSession.getInstance();
            Customer currentCustomer = new Customer(
                    session.getId(),
                    session.getPhone(),
                    session.getName(),
                    session.getUsername(),
                    session.getPoint()
            );

            controller.setCustomerData(currentCustomer);

            if (isViewOnly) {
                controller.setViewOnlyMode();
            } else {
                controller.setProfileEditMode();
            }

            Stage stage = new Stage();
            stage.setTitle(isViewOnly ? "Thông tin tài khoản" : "Chỉnh sửa tài khoản");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            if (controller.isSaveSuccess()) {
                loadUserProfile();
                Util.Others.showAlert(mainBorderPane, "Cập nhật dữ liệu cá nhân thành công!", false);
            }

        } catch (Exception e) {
            System.out.println("Lỗi khi mở form thông tin cá nhân: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

