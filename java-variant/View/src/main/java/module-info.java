module org.kacperandtobiasz.view {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.kacperandtobiasz.view to javafx.fxml;
    exports org.kacperandtobiasz.view;
}