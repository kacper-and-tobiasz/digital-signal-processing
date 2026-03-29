module org.kacperandtobiasz.signalanalyzer {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.kacperandtobiasz.signalanalyzer to javafx.fxml;
    exports org.kacperandtobiasz.signalanalyzer;
}