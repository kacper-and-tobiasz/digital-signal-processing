package org.kacperandtobiasz.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    private final MainContext mainContext = new MainContext();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
        fxmlLoader.setControllerFactory(type -> new MainController(mainContext));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Cyfrowe przetwarzanie sygnałów - Kacper Majkowski i Tobiasz Kowalczyk");
        stage.setScene(scene);
        stage.show();
    }
}
