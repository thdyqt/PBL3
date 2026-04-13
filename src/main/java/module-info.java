module com.example.pbl3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires jbcrypt;
    requires com.zaxxer.hikari;

    opens EntityDTO to javafx.base;
    opens GUI to javafx.fxml;
    exports GUI;
    exports GUI.Staff;
    opens GUI.Staff to javafx.fxml;
    exports GUI.Customer;
    opens GUI.Customer to javafx.fxml;
}