package GUI.Staff;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class ProductController {
    @FXML
    private Button btnBack;
    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDisable;

    @FXML
    private Button btnEdit;

    @FXML
    private ComboBox<?> cbCategory;

    @FXML
    private TableColumn<?, ?> colCategoryID;

    @FXML
    private TableColumn<?, ?> colImage;

    @FXML
    private TableColumn<?, ?> colIsAvailable;

    @FXML
    private TableColumn<?, ?> colProductID;

    @FXML
    private TableColumn<?, ?> colProductName;

    @FXML
    private TableColumn<?, ?> colProductPrice;

    @FXML
    private TableColumn<?, ?> colQuantity;

    @FXML
    private TableColumn<?, ?> colSTT;

    @FXML
    private Label lblCount;

    @FXML
    private Label lblStatus;

    @FXML
    private TableView<?> tblProduct;

    @FXML
    private TextField txtSearch;
    private StackPane contentArea;

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }
    @FXML
    void handleAdd(ActionEvent event) {

    }

    @FXML
    void handleDisable(ActionEvent event) {

    }

    @FXML
    void handleEdit(ActionEvent event) {

    }
    @FXML
    void handleBack(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("productMenuView_fixed.fxml"));
            Node node = loader.load();

            // Truyền lại contentArea cho productMenuController
            productMenuController ctrl = loader.getController();
            if (contentArea != null) {
                ctrl.setContentArea(contentArea);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(node);
        } catch (Exception e) {
            System.out.println("Lỗi khi quay lại productMenu: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
