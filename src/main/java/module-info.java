module org.arkanoidpackage.arkanoid {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.arkanoidpackage.arkanoid to javafx.fxml;
    exports org.arkanoidpackage.arkanoid;
    exports org.arkanoidpackage.lib;
}
