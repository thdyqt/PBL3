package GUI.Staff;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import  javafx.scene.Node;

public class productMenuController {

    @FXML
    private Button btnCategory;

    @FXML
    private StackPane btnCategoryPane;

    @FXML
    private Button btnProductM;

    @FXML
    private StackPane btnProductPane;
    private StackPane contentArea;

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }
    @FXML
    void handleCategoryManagement(ActionEvent event) {
        switchForm("categoryView.fxml");

    }

    @FXML
    void handleProductManagement(ActionEvent event) {
        switchForm("productView.fxml");
    }
    private void switchForm(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Node node = loader.load();
            Object ctrl = loader.getController();
            if (ctrl instanceof ProductController pc) {
                pc.setContentArea(this.contentArea);
            }
            contentArea.getChildren().clear();
            contentArea.getChildren().add(node);
        } catch (Exception e) {
            System.out.println("Lỗi khi tải trang: " + fxmlFileName);
            e.printStackTrace();
        }
    }
}
