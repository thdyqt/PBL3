package GUI.Staff;

import Util.IContentArea;
import Util.Others;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

import static Util.Others.showAlert;

public class ProductMenuController implements Initializable, IContentArea {
    @FXML
    private Button btnCategory;

    @FXML
    private StackPane btnCategoryPane;

    @FXML
    private Button btnProductM;

    @FXML
    private StackPane btnProductPane;

    private StackPane contentArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Others.playButtonAnimation(btnProductM);
        Others.playButtonAnimation(btnCategory);
    }

    @Override
    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    @FXML
    void handleCategoryManagement(ActionEvent event) {
      switchForm("/GUI/Staff/CategoryManagement.fxml");
    }

    @FXML
    void handleProductManagement(ActionEvent event) {
        switchForm("/GUI/Staff/ProductManagement.fxml");
    }

    private void switchForm(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Node node = loader.load();
            Object ctrl = loader.getController();

            if (ctrl instanceof IContentArea ic) {
                ic.setContentArea(this.contentArea);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(node);

        } catch (Exception e) {
            e.printStackTrace();

            if (contentArea != null) {
                showAlert(contentArea, "Lỗi khi tải trang: " + fxmlFileName, true);
            }
        }
    }
}