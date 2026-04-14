package GUI.Staff;

import BusinessBLL.CategoryBusiness;
import BusinessBLL.ProductBusiness;
import EntityDTO.Category;
import EntityDTO.OrderDetail;
import EntityDTO.Product;
import Util.Others;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static Util.Others.vn;

public class POS implements Initializable {
    @FXML
    private HBox mainPane;

    @FXML
    private Button btnAddCustomer;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnPayment;

    @FXML
    private ComboBox<Category> cbbCategory;

    @FXML
    private ComboBox<EntityDTO.PromoCode> cbbDiscount;

    @FXML
    private TableView<OrderDetail> tableCart;

    @FXML
    private TableColumn<OrderDetail, Integer> colSTT;

    @FXML
    private TableColumn<OrderDetail, String> colProductName;

    @FXML
    private TableColumn<OrderDetail, Integer> colQuantity;

    @FXML
    private TableColumn<OrderDetail, Integer> colPrice;

    @FXML
    private TableColumn<OrderDetail, Integer> colTotal;

    @FXML
    private TableColumn<EntityDTO.OrderDetail, Void> colAction;

    @FXML
    private FlowPane flowPaneProducts;

    @FXML
    private Label lblCustomerName;

    @FXML
    private Label lblSubtotal;

    @FXML
    private Label lblTotalPay;

    @FXML
    private Label lblTotalQuantity;

    @FXML
    private TextField txtCustomerPhone;

    @FXML
    private TextField txtSearchProduct;

    private ObservableList<OrderDetail> cartList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colSTT.setCellFactory(column -> new TableCell<EntityDTO.OrderDetail, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        colProductName.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getProduct().getProductName());
        });

        colProductName.setCellFactory(tc -> {
            TableCell<EntityDTO.OrderDetail, String> cell = new TableCell<>() {
                private javafx.scene.text.Text text = new javafx.scene.text.Text();

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        text.setText(item);
                        text.wrappingWidthProperty().bind(tc.widthProperty().subtract(10));
                        text.setStyle("-fx-fill: #1E293B; -fx-font-size: 13px;");
                        setGraphic(text);
                    }
                }
            };
            return cell;
        });

        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setCellFactory(column -> new TableCell<EntityDTO.OrderDetail, Integer>() {
            @Override
            protected void updateItem(Integer price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format(vn, "%,d", price));
                }
            }
        });

        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colTotal.setCellFactory(column -> new TableCell<EntityDTO.OrderDetail, Integer>() {
            @Override
            protected void updateItem(Integer price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format(vn, "%,d", price));
                }
            }
        });

        colQuantity.setCellFactory(column -> new TableCell<EntityDTO.OrderDetail, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    EntityDTO.OrderDetail currentItem = getTableRow().getItem();

                    HBox hbox = new HBox(8);
                    hbox.setAlignment(Pos.CENTER);

                    // Nút Trừ
                    Button btnMinus = new Button("-");
                    btnMinus.setStyle("-fx-background-color: #F1F5F9; -fx-background-radius: 5; -fx-font-weight: bold;");
                    btnMinus.setCursor(Cursor.HAND);
                    btnMinus.setOnAction(e -> {
                        if (currentItem.getQuantity() > 1) {
                            currentItem.setQuantity(currentItem.getQuantity() - 1);
                            currentItem.setPrice(currentItem.getPrice());
                            tableCart.refresh();
                            calculateTotal();
                        }
                    });

                    // Label hiển thị số
                    Label lblQty = new Label(String.valueOf(currentItem.getQuantity()));
                    lblQty.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
                    lblQty.setMinWidth(20);
                    lblQty.setAlignment(Pos.CENTER);

                    // Nút Cộng
                    Button btnPlus = new Button("+");
                    btnPlus.setStyle("-fx-background-color: #F1F5F9; -fx-background-radius: 5; -fx-font-weight: bold;");
                    btnPlus.setCursor(Cursor.HAND);
                    btnPlus.setOnAction(e -> {
                        if (currentItem.getQuantity() < currentItem.getProduct().getQuantity()) {
                            currentItem.setQuantity(currentItem.getQuantity() + 1);
                            currentItem.setPrice(currentItem.getPrice());
                            tableCart.refresh();
                            calculateTotal();
                        } else {
                            Others.showAlert(mainPane, "Kho chỉ còn " + currentItem.getProduct().getQuantity() + " sản phẩm!", true);
                        }
                    });

                    hbox.getChildren().addAll(btnMinus, lblQty, btnPlus);
                    setGraphic(hbox);
                }
            }
        });

        // --- CỘT XÓA---
        colAction.setCellFactory(column -> new TableCell<EntityDTO.OrderDetail, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Button btnDelete = new Button("🗑");

                    btnDelete.setStyle("-fx-background-color: transparent; " +
                            "-fx-text-fill: #EF4444; " +
                            "-fx-font-size: 18px; " +
                            "-fx-padding: 0;");

                    btnDelete.setCursor(Cursor.HAND);

                    btnDelete.setOnMouseEntered(e -> btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #B91C1C; -fx-font-size: 20px; -fx-padding: 0;"));
                    btnDelete.setOnMouseExited(e -> btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #EF4444; -fx-font-size: 18px; -fx-padding: 0;"));

                    btnDelete.setOnAction(e -> {
                        OrderDetail currentItem = getTableRow().getItem();
                        cartList.remove(currentItem);
                        calculateTotal();
                    });

                    setGraphic(btnDelete);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        tableCart.setItems(cartList);
        loadCategories();
        loadProducts();
    }

    private void loadCategories() {
        List<Category> listCat = CategoryBusiness.getAllCategories();
        cbbCategory.setItems(FXCollections.observableArrayList(listCat));
    }

    private void loadProducts() {
        List<Product> listPro = ProductBusiness.getAllProducts();

        flowPaneProducts.getChildren().clear();

        for (Product sp : listPro) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Staff/ProductCard.fxml"));
                VBox card = loader.load();

                ProductCardController controller = loader.getController();
                controller.setData(sp);

                card.setOnMouseClicked(event -> {
                    ScaleTransition scaleT = new ScaleTransition(Duration.millis(100), card);
                    scaleT.setFromX(1.0); scaleT.setFromY(1.0);
                    scaleT.setToX(0.93); scaleT.setToY(0.93);
                    scaleT.setAutoReverse(true);
                    scaleT.setCycleCount(2);
                    scaleT.play();
                    addToCart(sp);
                });

                flowPaneProducts.getChildren().add(card);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addToCart(Product selectedProduct) {
        if (selectedProduct.getQuantity() <= 0 ) {
            Others.showAlert(mainPane, "Sản phẩm này đã hết hàng.", true);
            return;
        }

        boolean isAlreadyInCart = false;
        for (OrderDetail item : cartList) {
            if (item.getProduct().getProductID() == selectedProduct.getProductID()) {
                if (item.getQuantity() == selectedProduct.getQuantity()) {
                    Others.showAlert(mainPane, "Không đủ số lượng tồn kho.", true);
                    return;
                }

                item.setQuantity(item.getQuantity() + 1);
                item.setPrice(item.getPrice());
                isAlreadyInCart = true;
                break;
            }
        }

        if (!isAlreadyInCart) {
            OrderDetail newItem = new OrderDetail();
            newItem.setProduct(selectedProduct);
            newItem.setQuantity(1);
            newItem.setPrice(selectedProduct.getProductPrice());

            cartList.add(newItem);
        }

        tableCart.refresh();
        calculateTotal();
    }

    private void calculateTotal() {
        int subtotal = 0;
        int totalQty = 0;

        for (OrderDetail item : cartList) {
            subtotal += item.getTotalPrice();
            totalQty += item.getQuantity();
        }

        lblSubtotal.setText(String.format(vn, "%,d VNĐ", subtotal));
        lblTotalQuantity.setText(totalQty + " món");

        lblTotalPay.setText(String.format(vn, "%,d VNĐ", subtotal));
    }

    @FXML
    void handleAddCustomer(ActionEvent event) {

    }

    @FXML
    void handleCancelOrder(ActionEvent event) {

    }

    @FXML
    void handlePayment(ActionEvent event) {

    }
}
