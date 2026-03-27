package Util;

import javafx.animation.*;
import javafx.scene.*;
import javafx.util.*;

public class Others {
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
}
