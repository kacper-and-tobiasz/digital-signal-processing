module org.kacperandtobiasz.view {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kacperandtobiasz.model;


    opens org.kacperandtobiasz.view to javafx.fxml;
    exports org.kacperandtobiasz.view;
}