package org.kacperandtobiasz.view;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        // This is the real starting point for JavaFX app.
        // I think we can safely ignore the inner workings
        // and just accept that we pass MainApplication.class
        // as an argument to the launch method.
        Application.launch(MainApplication.class, args);
    }
}
