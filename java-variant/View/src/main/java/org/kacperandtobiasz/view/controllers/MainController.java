package org.kacperandtobiasz.view.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.kacperandtobiasz.model.base.SignalRepository;
import org.kacperandtobiasz.model.base.signal.Signal;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.Scene;
import org.kacperandtobiasz.view.MainContext;

public class MainController {
    private final SignalRepository signalRepo;

    @FXML
    public TabPane main_tabpane;
//    @FXML
//    public Tab editorTab;
//    @FXML
//    public Tab operationsTab;

    private final ObservableList<Signal> signals = FXCollections.observableArrayList();

    public MainController(MainContext mainContext) {
        this.signalRepo = mainContext.signalRepository();

        // Replacing repo inner list to make data binding possible.
        this.signalRepo.setBackingList(signals);
    }

    private void setupEscapeKeyFocusReset(){
        Platform.runLater(() -> {
            Scene scene = main_tabpane.getScene();
            if (scene != null) {
                scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        scene.getRoot().requestFocus();
                    }
                });
            }
        });
    }

    // Called after scene graph has been loaded and objects are accessible for post-processing.
    @FXML
    private void initialize() {
        setupEscapeKeyFocusReset();
        setupSignalDisplayLogicBasedOnSelectedTabPane();
    }

    private void setupSignalDisplayLogicBasedOnSelectedTabPane() {
        main_tabpane.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
//            if(newValue.equals(editorTab)){
//
//            }
        });
    }


}
