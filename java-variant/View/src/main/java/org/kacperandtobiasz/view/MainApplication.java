package org.kacperandtobiasz.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApplication extends Application {
    private final MainContext mainContext = new MainContext();

    // Application.start() is a method called by JavaFX in the process of starting an app.
    // You can read more about lifecycle of JavaFX app here: https://docs.oracle.com/javase/8/javafx/api/javafx/application/Application.html
    @Override
    public void start(Stage stage) throws IOException {

        // A simpler way would be to call 
        //  FXMLLoader.load(this.getClass().getResource("main.fxml")),
        // but that would not allow for dependency injection (DI).

        // Used for loading FXML object hierarchy from file.
        // This hierarchy is called a 'scene graph'.
        FXMLLoader loader = new FXMLLoader();

        // '.fxml' file we want is in the resources folder.
        // To access it, we call .getResource(fileName) on a class that's in the module
        // our resource is located in.
        URL mainFxml = this.getClass().getResource("main.fxml");

        // We're telling out loader where to load the '.fxml' from.
        loader.setLocation(mainFxml);

        // FXMLLoader binds FXML objects to fields of a given controller using reflection at load time.
        // That's why we have to specify the controller before loading the scene graph. (at least I think that's why).
        loader.setController(new MainController(mainContext)); // this is where DI happens

        // Parent class is the base class for all nodes that have children in the scene graph.
        // It's responsible for a few things (consult JavaDoc for more detail), but
        // we can think of it as root of the scene graph (the hierarchy).
        Parent sceneGraph = loader.load();
        
        // Scene is a container for all content in a scene graph.
        // It's basically a runtime for the scene graph and manages a few important things.
        // https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Scene.html
        Scene scene = new Scene(sceneGraph);

        URL mainCss = this.getClass().getResource("main.css");
        if (mainCss != null) {
            scene.getStylesheets().add(mainCss.toExternalForm());
        }

        // Stage is simply an application window.
        stage.setTitle("Cyfrowe przetwarzanie sygnałów - Kacper Majkowski i Tobiasz Kowalczyk");
        stage.setScene(scene);

        // This actually triggers the scene rendering and shows the window.
        stage.show();
    }
}
