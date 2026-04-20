package GUI.Customer;

import EntityDTO.Customer;
import GUI.CustomerDialogController;
import Util.CartManager;
import Util.IContentArea;
import Util.Others;
import Util.UserSession;
import javafx.animation.*;
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
import javafx.scene.layout.Pane;
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

    @FXML private Button btnLoginGuest;

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
    private Label lblCartBadge;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private MenuButton menuAcc;

    @FXML
    private CustomMenuItem menuEditAcc;

    @FXML
    private CustomMenuItem menuInfo;

    @FXML
    private Pane slideIndicator;

    @FXML
    private StackPane cartContainer;

    private Button[] menuButtons;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainBorderPane.setOpacity(0);
        mainBorderPane.setScaleX(0.95);
        mainBorderPane.setScaleY(0.95);

        // Tạo hiệu ứng rõ dần (Fade)
        FadeTransition fade = new FadeTransition(Duration.millis(500), mainBorderPane);
        fade.setToValue(1);

        // Tạo hiệu ứng phóng to về kích thước chuẩn (Scale)
        ScaleTransition scale = new ScaleTransition(Duration.millis(500), mainBorderPane);
        scale.setToX(1.0);
        scale.setToY(1.0);

        ParallelTransition pt = new ParallelTransition(fade, scale);
        pt.setInterpolator(Interpolator.EASE_OUT);
        pt.play();

        CartManager.getInstance().customerTotalCountProperty().addListener((observable, oldValue, newValue) -> {
            int count = newValue.intValue();
            if (count > 0) {
                lblCartBadge.setText(String.valueOf(count));
                lblCartBadge.setVisible(true);
            } else {
                lblCartBadge.setVisible(false);
            }
        });

        if (UserSession.getInstance().isGuest()) {
            menuAcc.setVisible(false);
            menuAcc.setManaged(false);
            btnExit.setVisible(false);
            btnExit.setManaged(false);

            btnLoginGuest.setVisible(true);
            btnLoginGuest.setManaged(true);

        } else {
            btnLoginGuest.setVisible(false);
            btnLoginGuest.setManaged(false);

            menuAcc.setVisible(true);
            menuAcc.setManaged(true);
            btnExit.setVisible(true);
            btnExit.setManaged(true);

            menuInfo.setOnAction(event -> openProfileDialog(true));
            menuEditAcc.setOnAction(event -> openProfileDialog(false));
            loadUserProfile();
        }

        menuButtons = new Button[]{btnHome, btnProducts, btnCart, btnOrders};
        setActiveMenu(btnHome);
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
            if (btn != null) btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #475569; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 25;");
        }

        if (activeButton != null) {
            activeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #2563EB; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 25;");
            double targetX = activeButton.getBoundsInParent().getMinX();
            double targetWidth = activeButton.getBoundsInLocal().getWidth();

            if (activeButton == btnCart) {
                targetX = cartContainer.getBoundsInParent().getMinX();
                targetWidth = cartContainer.getBoundsInLocal().getWidth();
            }

           Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(250),
                            new KeyValue(slideIndicator.translateXProperty(), targetX, Interpolator.EASE_BOTH),
                            new KeyValue(slideIndicator.prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH)
                    )
            );
            timeline.play();
        }
    }

    private void switchForm(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Node node = loader.load();
            Object controller = loader.getController();
            if (controller instanceof IContentArea ctrl) {
                ctrl.setContentArea(this.contentArea);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(node);
        } catch (Exception e) {
            System.out.println("Lỗi khi tải trang: " + fxmlFileName);
            e.printStackTrace();
        }
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Login.fxml"));
                mainBorderPane.getScene().setRoot(loader.load());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnLoginGuestClick(ActionEvent event) {
        UserSession.getInstance().clearSession();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Login.fxml"));
            Parent root = loader.load();

            btnLoginGuest.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            Others.showAlert(mainBorderPane, "Lỗi khi quay lại trang đăng nhập!", true);
        }
    }

    @FXML
    void btnHomeClick(ActionEvent event) {
        setActiveMenu(btnHome);
    }

    @FXML
    void btnProductsClick(ActionEvent event) {
        setActiveMenu(btnProducts);
        switchForm("/GUI/Customer/CustomerProduct.fxml");
    }

    @FXML
    void btnOrdersClick(ActionEvent event) {
        setActiveMenu(btnOrders);
    }

    @FXML
    void btnCartClick(ActionEvent event) {
        setActiveMenu(btnCart);
    }

    private void openProfileDialog(boolean isViewOnly) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/CustomerDialog.fxml"));
            Parent root = loader.load();

            CustomerDialogController controller = loader.getController();

            UserSession session = UserSession.getInstance();
            Customer currentCustomer = new Customer(
                    session.getId(),
                    session.getPhone(),
                    session.getName(),
                    session.getUsername(),
                    session.getAddress(),
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

