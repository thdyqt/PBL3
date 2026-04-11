package Util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Others {
    // SET ĐỘ DÀI TỐI ĐA CHO TEXTFIELD
    public static void setMaxLength(javafx.scene.control.TextField textField, int maxLength) {
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

    // HIỂN THỊ HỘP THOẠI XÁC NHẬN
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
}
