package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DashboardForm {
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
}
