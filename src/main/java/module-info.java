module com.example.truststock {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires java.desktop;
    requires javafx.graphics;

    opens com.example.truststock to javafx.fxml;
    opens com.example.truststock.staff to javafx.fxml;
    opens com.example.truststock.db to javafx.fxml;
    opens com.example.truststock.customer to javafx.fxml;
    opens com.example.truststock.workcontrol to javafx.fxml;


    opens com.example.truststock.model to javafx.base;


    exports com.example.truststock;
    exports com.example.truststock.staff;
    exports com.example.truststock.workcontrol;
}
