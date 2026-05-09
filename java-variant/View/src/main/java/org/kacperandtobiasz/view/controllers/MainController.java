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

//    private void setupParameterUpdateOnSignalChange() {
//        signal_selector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
//            if (newVal != null) {
//                signal_name.setText(newVal.getName());
//                selectedSignalSampled.set(newVal.isSampled());
//
//                if (newVal.getGenerator() != null) {
//                    SignalParameters params = newVal.getGenerator().getParameters();
//                    signal_type.getSelectionModel().select(newVal.getGenerator().getSignalType());
//
//                    amplitude.getValueFactory().setValue(params.getAmplitude());
//                    if (signal_start != null) signal_start.getValueFactory().setValue(params.getStartTime());
//                    if (signal_duration != null) signal_duration.getValueFactory().setValue(params.getDuration());
//
//                    if (base_period != null) base_period.getValueFactory().setValue(params.getPeriod());
//                    if (duty_cycle != null) duty_cycle.getValueFactory().setValue(params.getDutyCycle());
//                    if (jump_time != null) jump_time.getValueFactory().setValue(params.getJumpTime());
//                    if (probability != null) probability.getValueFactory().setValue(params.getProbability());
//
//                    if (first_sample != null) first_sample.getValueFactory().setValue(params.getFirstSample());
//                    if (jump_sample != null) jump_sample.getValueFactory().setValue(params.getJumpSample());
//                    if (sample_length != null) sample_length.getValueFactory().setValue(params.getSampleLength());
//                }
//                if (sampling_rate != null) sampling_rate.getValueFactory().setValue(newVal.getSamplingFrequency());
//
//                drawSignal(newVal, signal_chart, signal_bar_chart);
//            } else {
//                selectedSignalSampled.set(false);
//                drawSignal(null, signal_chart, signal_bar_chart);
//            }
//        });
//    }
//
}
