package GUI.Staff;

import BusinessBLL.CategoryBusiness;
import BusinessBLL.CustomerBusiness;
import BusinessBLL.ProductBusiness;
import BusinessBLL.PromoCodeBusiness;
import EntityDTO.*;
import Util.CartManager;
import Util.Others;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
    private ComboBox<PromoCode> cbbDiscount;

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
    private Label lblDiscountTitle;

    @FXML
    private Label lblDiscountAmount;

    @FXML
    private TextField txtCustomerPhone;

    @FXML
    private TextField txtSearchProduct;

    private ObservableList<OrderDetail> cartList;
    private List<Product> allProducts;
    private Customer currentCustomer = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Others.setMaxLength(txtCustomerPhone, 10);

        txtCustomerPhone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtCustomerPhone.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

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
                    setText(Others.formatPrice(price));
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
                    setText(Others.formatPrice(price));
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

        cbbDiscount.valueProperty().addListener((observable, oldValue, newValue) -> {
            calculateTotal();
        });

        allProducts = ProductBusiness.getAllProducts();
        cartList = CartManager.getInstance().getPosCart();
        tableCart.setItems(cartList);
        loadCategories();
        loadPromoCodes();
        setupSearchAndFilter();
        displayProducts(allProducts);
    }

    private void loadCategories() {
        List<Category> listCat = CategoryBusiness.getAllCategories();
        Category allCat = new Category(0, "Tất cả sản phẩm", "Active");
        listCat.add(0, allCat);

        cbbCategory.setItems(FXCollections.observableArrayList(listCat));
        cbbCategory.getSelectionModel().selectFirst();
    }

    private void loadPromoCodes() {
        List<PromoCode> listCode = PromoCodeBusiness.getAllActivePromoCodes();
        PromoCode allCode = new PromoCode("Không có", "Không có", 0, "AMOUNT", 0, null, null, "Active");
        listCode.add(0, allCode);

        cbbDiscount.setItems(FXCollections.observableArrayList(listCode));
        cbbDiscount.getSelectionModel().selectFirst();
    }

    private void displayProducts(List<Product> listPro) {
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

                FadeTransition ft = new FadeTransition(Duration.millis(400), card);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();
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
        int discountPercent = 0;
        int discountAmount = 0;

        for (OrderDetail item : cartList) {
            subtotal += item.getTotalPrice();
            totalQty += item.getQuantity();
        }

        lblSubtotal.setText(Others.formatPrice(subtotal));
        lblTotalQuantity.setText(totalQty + " món");

        int customerDiscount = CustomerBusiness.getDiscountPercent(currentCustomer);
        PromoCode selectedPromo = cbbDiscount.getValue();

        if (selectedPromo != null && !selectedPromo.getCode().equals("Không có")) {
            if (subtotal < selectedPromo.getMinOrderValue()) {
                Others.showAlert(mainPane, "Đơn hàng chưa đạt giá trị tối thiểu của mã giảm giá.", true);
                Platform.runLater(() -> cbbDiscount.getSelectionModel().selectFirst());
            }
            else if (selectedPromo.getDiscountType().trim().equalsIgnoreCase("PERCENT")) {
                int promoCodeDiscount = selectedPromo.getDiscountValue();
                discountPercent = customerDiscount + promoCodeDiscount;
                lblDiscountTitle.setText(String.format("Giảm giá (%d%%):", discountPercent));
                discountAmount = (int)(subtotal * (discountPercent / 100.0));
            }
            else {
                int promoCodeDiscount = selectedPromo.getDiscountValue();
                discountPercent = customerDiscount;
                lblDiscountTitle.setText(String.format("Giảm giá (%d%% + %,dđ):", discountPercent, promoCodeDiscount));
                discountAmount = (int)(subtotal * (discountPercent / 100.0) + promoCodeDiscount);
            }
        }
        else {
            discountPercent = customerDiscount;
            lblDiscountTitle.setText(String.format("Giảm giá (%d%%):", discountPercent));
            discountAmount = (int)(subtotal * (discountPercent / 100.0));
        }

        if (discountAmount > subtotal) {
            discountAmount = subtotal;
        }

        int finalTotal = subtotal - discountAmount;

        lblDiscountAmount.setText(Others.formatPrice(discountAmount));
        lblTotalPay.setText(Others.formatPrice(finalTotal));
    }

    private void setupSearchAndFilter() {
        txtSearchProduct.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProducts();
        });

        cbbCategory.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterProducts();
        });
    }

    private void filterProducts() {
        String searchText = txtSearchProduct.getText().toLowerCase().trim();
        Category selectedCategory = cbbCategory.getValue();

        List<Product> filteredList = new ArrayList<>();

        for (Product p : allProducts) {
            boolean matchName = p.getProductName().toLowerCase().contains(searchText) ||
                    String.valueOf(p.getProductID()).contains(searchText);

            boolean matchCategory = true;

            if (selectedCategory != null && selectedCategory.getCategoryID() != 0) {
                matchCategory = (p.getCategoryID() == selectedCategory.getCategoryID());
            }

            if (matchName && matchCategory) {
                filteredList.add(p);
            }
        }

        displayProducts(filteredList);
    }

    @FXML
    void searchCustomerByPhone() {
        String phone = txtCustomerPhone.getText().trim();

        if (!phone.matches("^0[0-9]{9}$")) {
            Others.showAlert(mainPane, "Số điện thoại không hợp lệ!", true);
            currentCustomer = null;
            lblCustomerName.setText("Khách vãng lai");
            lblCustomerName.setStyle("-fx-text-fill: #64748B;");
            txtCustomerPhone.requestFocus();
            calculateTotal();
            return;
        }

        currentCustomer = CustomerBusiness.findCustomer(phone);

        if (currentCustomer == null) {
            Others.showAlert(mainPane, "Số điện thoại không tồn tại, vui lòng đăng ký!", true);
            lblCustomerName.setText("Khách vãng lai");
            lblCustomerName.setStyle("-fx-text-fill: #64748B;");
            txtCustomerPhone.requestFocus();
            calculateTotal();
            return;
        }

        lblCustomerName.setText(currentCustomer.getName());
        lblCustomerName.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold;");
        calculateTotal();
    }
    @FXML
    void handleAddCustomer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/CustomerDialog.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Thêm khách hàng mới");
            stage.setScene(new Scene(root));

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            searchCustomerByPhone();

        } catch (Exception e) {
            e.printStackTrace();
            Others.showAlert(mainPane, "Lỗi khi mở form thêm khách hàng!", true);
        }
    }

    @FXML
    void handleCancelOrder(ActionEvent event) {
        if (cartList.isEmpty() && currentCustomer == null) {
            return;
        }

        if (Others.showCustomConfirm("Xác nhận hủy đơn", "Bạn có chắc chắn muốn hủy toàn bộ đơn hàng hiện tại không?", "Đồng ý", "Hủy")){
            txtCustomerPhone.clear();
            currentCustomer = null;
            lblCustomerName.setText("Khách vãng lai");
            lblCustomerName.setStyle("-fx-text-fill: #64748B;");

            cartList.clear();
            tableCart.refresh();
            cbbCategory.getSelectionModel().clearSelection();
            cbbDiscount.getSelectionModel().clearSelection();
            calculateTotal();
            txtSearchProduct.clear();
        }
    }

    @FXML
    void handlePayment(ActionEvent event) {
        if (cartList.isEmpty()) {
            Others.showAlert(mainPane, "Giỏ hàng đang trống, không thể thanh toán!", true);
            return;
        }

        String totalPayStr = lblTotalPay.getText().replaceAll("[^0-9]", "");
        int finalTotal = Integer.parseInt(totalPayStr);

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30, 40, 30, 40));

        root.getStyleClass().add("modern-alert");

        Label lblTitle = new Label("CHỌN PHƯƠNG THỨC THANH TOÁN");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        Label lblAmount = new Label(Others.formatPrice(finalTotal));
        lblAmount.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");

        HBox btnBox = new HBox(20);
        btnBox.setAlignment(Pos.CENTER);

        Button btnCash = new Button("💵 Tiền mặt");
        btnCash.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 25; -fx-background-radius: 8; -fx-cursor: hand;");

        Button btnTransfer = new Button("📱 Chuyển khoản");
        btnTransfer.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 25; -fx-background-radius: 8; -fx-cursor: hand;");

        btnBox.getChildren().addAll(btnCash, btnTransfer);

        Button btnCancel = new Button("Hủy bỏ");
        btnCancel.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B; -fx-font-size: 14px; -fx-cursor: hand; -fx-underline: true;");

        root.getChildren().addAll(lblTitle, lblAmount, btnBox, btnCancel);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        try {
            scene.getStylesheets().add(getClass().getResource("/GUI/style.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("Không load được CSS cho popup thanh toán.");
        }
        dialogStage.setScene(scene);

        btnCancel.setOnAction(e -> dialogStage.close());

        btnCash.setOnAction(e -> {
            dialogStage.close();
            // processCheckout("Tiền mặt", finalTotal);
        });

        btnTransfer.setOnAction(e -> {
            dialogStage.close();
            showVietQRAndCheckout(finalTotal);
        });

        dialogStage.showAndWait();
    }

    private void showVietQRAndCheckout(int amount) {
        try {
            String bankID = "MB";
            String accountNo = "000002907";
            String accountName = URLEncoder.encode("PHAN THANH DUY", "UTF-8").replace("+", "%20");
            String addInfo = URLEncoder.encode("Thanh toan don hang DUT Bakery", "UTF-8").replace("+", "%20");

            String qrUrl = String.format("https://img.vietqr.io/image/%s-%s-compact2.png?amount=%d&addInfo=%s&accountName=%s",
                    bankID, accountNo, amount, addInfo, accountName);

            Image qrImage = new Image(qrUrl, false);

            if (qrImage.isError()) {
                throw new Exception("Không thể tải ảnh QR từ máy chủ!");
            }

            ImageView imageView = new ImageView(qrImage);
            imageView.setFitWidth(500);
            imageView.setFitHeight(500);
            imageView.setPreserveRatio(true);

            Stage qrStage = new Stage();
            qrStage.initModality(Modality.APPLICATION_MODAL);
            qrStage.setTitle("Thanh toán Chuyển khoản");
            qrStage.setMaximized(true);

            VBox root = new VBox(30);
            root.setAlignment(Pos.CENTER);
            root.setStyle("-fx-background-color: #F8FAFC;");

            Label lblTitle = new Label("QUÉT MÃ ĐỂ THANH TOÁN");
            lblTitle.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

            Label lblAmount = new Label("Số tiền cần chuyển: " + Others.formatPrice(amount));
            lblAmount.setStyle("-fx-font-size: 28px; -fx-text-fill: #EF4444; -fx-font-weight: bold;");

            HBox btnBox = new HBox(20);
            btnBox.setAlignment(Pos.CENTER);

            Button btnCancel = new Button("Đóng / Hủy");
            btnCancel.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #EF4444; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 15 40; -fx-background-radius: 10; -fx-cursor: hand;");

            Button btnConfirm = new Button("✅ ĐÃ NHẬN ĐƯỢC TIỀN");
            btnConfirm.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 15 40; -fx-background-radius: 10; -fx-cursor: hand;");

            btnBox.getChildren().addAll(btnCancel, btnConfirm);
            root.getChildren().addAll(lblTitle, lblAmount, imageView, btnBox);

            Scene scene = new Scene(root);
            qrStage.setScene(scene);

            btnCancel.setOnAction(e -> qrStage.close());

            btnConfirm.setOnAction(e -> {
                qrStage.close();
                //processCheckout("Chuyển khoản", amount);
            });

            qrStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Others.showAlert(mainPane, "Lỗi khi tạo mã QR. Vui lòng kiểm tra mạng!", true);
        }
    }
}
