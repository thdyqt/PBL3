package GUI;

import Util.UserSession;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashboardForm implements Initializable {
    @FXML
    private Button btnBill;

    @FXML
    private Button btnCustomer;

    @FXML
    private Button btnExit;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnOnline;

    @FXML
    private Button btnOrder;

    @FXML
    private Button btnProduct;

    @FXML
    private Button btnStaff;

    @FXML
    private Button btnStatistic;

    @FXML
    private Button btnToggleSidebar;

    @FXML
    private StackPane contentArea;

    @FXML
    private Label lblName;

    @FXML
    private Label lblRole;

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

    @FXML
    private VBox sidebar;

    private Button[] menuButtons;
    private boolean isSidebarVisible = true;
    private Timeline sidebarTimeline;

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

        menuButtons = new Button[]{btnHome, btnOrder, btnOnline, btnBill, btnProduct, btnCustomer, btnStaff, btnStatistic};
        loadUserProfile();
        startClock();
        btnHomeClick(null);
    }

    // TOPBAR
    private void loadUserProfile() {
        String fullName = UserSession.getInstance().getName();
        String username = UserSession.getInstance().getUsername();
        String role = UserSession.getInstance().getPosition();

        if (fullName != null && !fullName.trim().isEmpty()) {
            lblName.setText(fullName + " (" + username + ")");

            String[] parts = fullName.trim().split("\\s+");
            String firstName = parts[parts.length - 1];
            String initial = String.valueOf(firstName.charAt(0)).toUpperCase();

            lblProfile.setText(initial);
        }

        if (role != null) {
            lblRole.setText(role);
        }
    }

    @FXML
    void btnToggleSidebarClick(ActionEvent event) {
        if (sidebarTimeline != null && sidebarTimeline.getStatus() == javafx.animation.Animation.Status.RUNNING) {
            return;
        }
        sidebarTimeline = new Timeline();

        if (isSidebarVisible) {
            KeyValue kvWidth = new KeyValue(sidebar.prefWidthProperty(), 0, Interpolator.EASE_BOTH);
            KeyValue kvMinWidth = new KeyValue(sidebar.minWidthProperty(), 0, Interpolator.EASE_BOTH);
            KeyValue kvOpacity = new KeyValue(sidebar.opacityProperty(), 0, Interpolator.EASE_BOTH);

            KeyFrame kf = new KeyFrame(Duration.millis(350), kvWidth, kvMinWidth, kvOpacity);
            sidebarTimeline.getKeyFrames().add(kf);

            sidebarTimeline.setOnFinished(e -> {
                sidebar.setVisible(false);
                sidebar.setManaged(false);
            });

            isSidebarVisible = false;
        } else {
            sidebar.setVisible(true);
            sidebar.setManaged(true);

            KeyValue kvWidth = new KeyValue(sidebar.prefWidthProperty(), 270, Interpolator.EASE_BOTH);
            KeyValue kvMinWidth = new KeyValue(sidebar.minWidthProperty(), 270, Interpolator.EASE_BOTH);
            KeyValue kvOpacity = new KeyValue(sidebar.opacityProperty(), 1, Interpolator.EASE_BOTH);

            KeyFrame kf = new KeyFrame(Duration.millis(350), kvWidth, kvMinWidth, kvOpacity);
            sidebarTimeline.getKeyFrames().add(kf);

            isSidebarVisible = true;
        }

        sidebarTimeline.play();
    }

    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a   |   dd/MM/yyyy");

        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    lblTime.setText(LocalDateTime.now().format(formatter));
                }),
                new KeyFrame(Duration.seconds(1))
        );

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    // SIDEBAR
    private void setActiveMenu(Button activeButton) {
        if (menuButtons == null) return;

        for (Button btn : menuButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("active-menu");
            }
        }

        if (activeButton != null) {
            activeButton.getStyleClass().add("active-menu");
        }
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
    void btnHomeClick(ActionEvent event) {
        setActiveMenu(btnHome);
    }

    @FXML
    void btnStaffClick(ActionEvent event) {
        setActiveMenu(btnStaff);
        switchForm("staffManagement.fxml");
    }

    @FXML
    void btnCustomerClick(ActionEvent event) {
        setActiveMenu(btnCustomer);
        switchForm("CustomerManagement.fxml");
    }

    @FXML
    void btnExitClick(ActionEvent event) {
        boolean isConfirm = Util.Others.showCustomConfirm(
                "Kết thúc ca trực",
                "Bạn đang yêu cầu đăng xuất khỏi hệ thống POS.\nBạn có chắc chắn muốn thoát không?",
                "Đăng xuất", "Hủy bỏ"
        );

        if (isConfirm) {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("login.fxml"));
                mainBorderPane.getScene().setRoot(loader.load());

                BusinessBLL.StaffBusiness.logout();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    void btnProductClick(ActionEvent event){
        setActiveMenu(btnProduct);
        switchForm("productMenuView.fxml");
    }
}

