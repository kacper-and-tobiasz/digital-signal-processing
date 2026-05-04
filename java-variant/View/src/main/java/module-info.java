module org.kacperandtobiasz.view {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kacperandtobiasz.model;
    requires javafx.graphics;


    opens org.kacperandtobiasz.view to javafx.fxml;
    exports org.kacperandtobiasz.view;
    exports org.kacperandtobiasz.view.controllers;
    opens org.kacperandtobiasz.view.controllers to javafx.fxml;
    exports org.kacperandtobiasz.view.controllers.editor;
    opens org.kacperandtobiasz.view.controllers.editor to javafx.fxml;
    exports org.kacperandtobiasz.view.controllers.operations;
    opens org.kacperandtobiasz.view.controllers.operations to javafx.fxml;
}