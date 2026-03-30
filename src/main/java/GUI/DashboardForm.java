package GUI;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    private Label lblPageTitle;

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
        menuButtons = new Button[]{btnHome, btnOrder, btnOnline, btnBill, btnProduct, btnCustomer, btnStaff, btnStatistic};
        startClock();
        btnHomeClick(null);
    }

    private void setActiveMenu(Button activeButton) {
        if (menuButtons == null) return;

        for (Button btn : menuButtons) {
            btn.getStyleClass().remove("active-menu");
        }

        if (activeButton != null) {
            activeButton.getStyleClass().add("active-menu");
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

    @FXML
    void btnHomeClick(ActionEvent event) {
        setActiveMenu(btnHome);
    }
}

