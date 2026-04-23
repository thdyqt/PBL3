package Util;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Others {
    private static final Map<String, Image> imageCache = new HashMap<>();
    private static final NumberFormat priceFormatter = NumberFormat.getNumberInstance(new java.util.Locale("vi", "VN"));

    // CHUẨN HÓA GIÁ TIỀN
    public static String formatPrice(int price) {
        return priceFormatter.format(price) + " đ";
    }

    // SET ĐỘ DÀI TỐI ĐA CHO TEXTFIELD
    public static void setMaxLength(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                textField.setText(oldValue);
            }
        });
    }

    // CHUẨN HÓA HỌ VÀ TÊN (KHI THÊM NHÂN VIÊN HOẶC KHÁCH HÀNG)
    public static String standardizeName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }

        String cleanedString = fullName.trim().replaceAll("\\s+", " ");
        String[] words = cleanedString.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            String firstLetter = word.substring(0, 1).toUpperCase();
            String remainingLetters = word.substring(1).toLowerCase();
            result.append(firstLetter).append(remainingLetters).append(" ");
        }

        return result.toString().trim();
    }

    // LẤY TÊN THAY VÌ HỌ
    public static String getFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts[parts.length - 1];
    }

    // LOAD IMAGE
    public static void loadImage(String imageName, ImageView imgView, int width, int height) {
        try {
            if (imageName == null || imageName.isEmpty()) imageName = "default.png";

            if (imageCache.containsKey(imageName)) {
                imgView.setImage(imageCache.get(imageName));
                return;
            }

            java.net.URL imageUrl = Others.class.getResource("/images/" + imageName);
            if (imageUrl != null) {
                javafx.scene.image.Image image = new javafx.scene.image.Image(imageUrl.toExternalForm(), width, height, true, true, true);
                imageCache.put(imageName, image);
                imgView.setImage(image);
            }
        } catch (Exception e) {
            System.err.println("Lỗi load ảnh: " + imageName);
        }
    }

    // CHAY THOI GIAN
    public static void startClock(Label label) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a   |   dd/MM/yyyy");

        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    label.setText(LocalDateTime.now().format(formatter));
                }),
                new KeyFrame(Duration.seconds(1))
        );

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    // ANIMATION KHI ẤN NÚT
    // Resize up and down slightly to make button clicking more tactile
    public static void playButtonAnimation(Node node){
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(50),node);
        scaleDown.setToX(0.90);
        scaleDown.setToY(0.90);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(50),node);
        scaleUp.setToX(1);
        scaleUp.setToY(1);

        node.setOnMousePressed(mouseEvent -> {
            scaleUp.stop();
            scaleDown.playFromStart();
        });

        node.setOnMouseReleased(mouseEvent -> {
            scaleDown.stop();
            scaleUp.playFromStart();
        });
    }

    public static void playHoverAnimation(Node node){
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100),node);
        scaleDown.setToX(0.95);
        scaleDown.setToY(0.95);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100),node);
        scaleUp.setToX(1);
        scaleUp.setToY(1);

        node.setOnMouseEntered(mouseEvent -> {
            scaleUp.stop();
            scaleDown.playFromStart();
        });

        node.setOnMouseExited(mouseEvent -> {
            scaleDown.stop();
            scaleUp.playFromStart();
        });
    }



    // ANIMATION KHI FORM HIỂN THỊ
    public static void playFormAnimation(Node formNode) {
        formNode.setOpacity(0);
        formNode.setTranslateY(50);

        // Hiệu ứng hiển thị rõ dần
        FadeTransition fade = new FadeTransition(Duration.millis(1500), formNode);
        fade.setFromValue(0);
        fade.setToValue(1);

        // Hiệu ứng dịch chuyển từ dưới lên
        TranslateTransition translate = new TranslateTransition(Duration.millis(1500), formNode);
        translate.setFromY(80);
        translate.setToY(0);

        ParallelTransition pt = new ParallelTransition(fade, translate);
        pt.play();
    }

    // ANIMATION KHI BẢNG HIỂN THỊ
    public static <T> void animateTableRows(TableView<T> tableView) {
        tableView.setRowFactory(tv -> {
            javafx.scene.control.TableRow<T> row = new javafx.scene.control.TableRow<>();

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && oldItem == null) {
                    row.setOpacity(0);
                    row.setTranslateX(50);

                    FadeTransition fade = new FadeTransition(Duration.millis(500), row);
                    fade.setToValue(1);

                    TranslateTransition slide = new TranslateTransition(Duration.millis(500), row);
                    slide.setToX(0);

                    ParallelTransition pt = new ParallelTransition(fade, slide);
                    pt.setInterpolator(Interpolator.EASE_OUT);

                    long delay = Math.min(row.getIndex() * 70, 800);
                    pt.setDelay(Duration.millis(delay));

                    pt.play();
                }
            });
            return row;
        });
    }

    // HIỂN THỊ THÔNG BÁO (NOTIFICATIONS)
    private static Label currentToast;
    private static SequentialTransition currentToastAnimation;

    public static void showAlert(Node node, String message, boolean isError) {
        if (node == null || node.getScene() == null) return;
        Pane rootPane = (Pane) node.getScene().getRoot();

        // Dọn dẹp thông báo cũ
        if (currentToast != null) {
            if (currentToastAnimation != null) {
                currentToastAnimation.stop();
            }
            rootPane.getChildren().remove(currentToast);
        }

        // Tạo thông báo mới
        Label toast = new Label(message);
        currentToast = toast;

        String bgColor = isError ? "#E53935" : "#43A047";
        toast.setStyle("-fx-background-color: " + bgColor + "; " +
                "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; " +
                "-fx-padding: 12 25; -fx-background-radius: 25; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        toast.setManaged(false);
        rootPane.getChildren().add(toast);
        toast.applyCss();
        toast.autosize();

        toast.layoutXProperty().bind(rootPane.widthProperty().subtract(toast.widthProperty()).divide(2));
        toast.setLayoutY(0);
        toast.setTranslateY(-50);
        toast.setOpacity(0);

        // Hiệu ứng Hiện ra (Trượt xuống)
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), toast);
        slideIn.setToY(30);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), toast);
        fadeIn.setToValue(1);
        ParallelTransition showAnim = new ParallelTransition(slideIn, fadeIn);

        // Hiệu ứng Biến mất (Trượt ngược lên lại)
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(400), toast);
        slideOut.setToY(-30);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setToValue(0);
        ParallelTransition hideAnim = new ParallelTransition(slideOut, fadeOut);

        // Đợi 2.5 giây rồi ẩn
        hideAnim.setDelay(Duration.seconds(2.5));
        hideAnim.setOnFinished(e -> {
            rootPane.getChildren().remove(toast);
            currentToast = null;
        });

        currentToastAnimation = new SequentialTransition(showAnim, hideAnim);
        currentToastAnimation.play();
    }

    // HIỂN THỊ HỘP THOẠI XÁC NHẬN (YES/NO)
    public static boolean showCustomConfirm(String title, String content, String btnYesText, String btnNoText) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        javafx.scene.layout.StackPane iconPane = new javafx.scene.layout.StackPane();
        javafx.scene.shape.Circle bg = new javafx.scene.shape.Circle(22, javafx.scene.paint.Color.web("#FEF3C7"));
        javafx.scene.control.Label exclamation = new javafx.scene.control.Label("!");
        exclamation.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #D97706;");
        iconPane.getChildren().addAll(bg, exclamation);
        alert.setGraphic(iconPane);

        // Tùy chỉnh chữ trên 2 nút bấm
        javafx.scene.control.ButtonType buttonYes = new javafx.scene.control.ButtonType(btnYesText, javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        javafx.scene.control.ButtonType buttonNo = new javafx.scene.control.ButtonType(btnNoText, javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        // Áp dụng CSS
        javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
        try {
            dialogPane.getStylesheets().add(Others.class.getResource("/GUI/style.css").toExternalForm());
            dialogPane.getStyleClass().add("modern-alert");
        } catch (Exception e) {
            System.out.println("Không tìm thấy file CSS cho Alert ở đường dẫn /GUI/style.css");
        }

        // Hiển thị và chờ người dùng bấm, sau đó trả về true/false
        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonYes;
    }

    // HỘP THOẠI XÁC NHẬN MẬT KHẨU
    public static boolean showPasswordConfirmDialog(Window ownerWindow, String newPassword) {
        final boolean[] isConfirmed = {false};

        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);

        if (ownerWindow != null) {
            stage.initOwner(ownerWindow);
        }

        VBox root = new VBox(12);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #E2E8F0; -fx-border-width: 1;");

        DropShadow shadow = new DropShadow(20, new Color(0, 0, 0, 0.15));
        root.setEffect(shadow);

        Label lblHeader = new Label("Xác nhận bảo mật");
        lblHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        Label lblDesc = new Label("Vui lòng xác nhận mật khẩu để lưu các thay đổi:");
        lblDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        PasswordField pwdCurrentField = new PasswordField();
        pwdCurrentField.setPromptText("Nhập mật khẩu hiện tại...");
        pwdCurrentField.setStyle("-fx-font-size: 14px; -fx-padding: 8; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #CBD5E1; -fx-background-color: #F8FAFC;");
        pwdCurrentField.setPrefWidth(320);

        PasswordField pwdConfirmNewField = new PasswordField();
        pwdConfirmNewField.setPromptText("Xác nhận lại mật khẩu mới...");
        pwdConfirmNewField.setStyle("-fx-font-size: 14px; -fx-padding: 8; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #CBD5E1; -fx-background-color: #F8FAFC;");
        pwdConfirmNewField.setPrefWidth(320);

        boolean isChangingPassword = newPassword != null && !newPassword.isEmpty();
        if (!isChangingPassword) {
            pwdConfirmNewField.setVisible(false);
            pwdConfirmNewField.setManaged(false);
        }

        Label lblError = new Label("");
        lblError.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: bold;");
        lblError.setPrefHeight(15);
        lblError.setWrapText(true);

        Button btnCancelPopup = new Button("Hủy bỏ");
        btnCancelPopup.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        btnCancelPopup.setOnAction(e -> stage.close());

        Button btnConfirmPopup = new Button("Xác nhận");
        btnConfirmPopup.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");

        Runnable checkAction = () -> {
            boolean isCurrentCorrect = pwdCurrentField.getText().equals(Util.UserSession.getInstance().getPassword());
            boolean isNewCorrect = !isChangingPassword || pwdConfirmNewField.getText().equals(newPassword);

            if (!isCurrentCorrect) {
                lblError.setText("Mật khẩu hiện tại không chính xác!");
                pwdCurrentField.clear();
                pwdCurrentField.requestFocus();
            } else if (!isNewCorrect) {
                lblError.setText("Mật khẩu xác nhận không trùng khớp với mật khẩu mới!");
                pwdConfirmNewField.clear();
                pwdConfirmNewField.requestFocus();
            } else {
                isConfirmed[0] = true;
                stage.close();
            }
        };

        btnConfirmPopup.setOnAction(e -> checkAction.run());
        pwdCurrentField.setOnAction(e -> {
            if (isChangingPassword) pwdConfirmNewField.requestFocus();
            else checkAction.run();
        });
        pwdConfirmNewField.setOnAction(e -> checkAction.run());

        HBox buttonBox = new HBox(10, btnCancelPopup, btnConfirmPopup);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(lblHeader, lblDesc, pwdCurrentField, pwdConfirmNewField, lblError, buttonBox);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        Platform.runLater(pwdCurrentField::requestFocus);
        stage.showAndWait();

        return isConfirmed[0];
    }
}
