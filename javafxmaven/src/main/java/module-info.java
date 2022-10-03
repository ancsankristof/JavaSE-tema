module com.example.javafxmaven {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.example.javafxmaven to javafx.fxml;
    exports com.example.javafxmaven;
    exports com.example.javafxmaven.Utils;
    opens com.example.javafxmaven.Utils to javafx.fxml;
}