package org.kacperandtobiasz.view.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import org.kacperandtobiasz.model.storage.SignalFileHandler;
import org.kacperandtobiasz.model.base.SignalRepository;
import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.model.base.signal.SignalFactory;
import org.kacperandtobiasz.model.base.signal.SignalParameters;
import org.kacperandtobiasz.model.base.signal.SignalType;
import org.kacperandtobiasz.model.base.signal.DiscreteSignal;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.Scene;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Slider;
import org.kacperandtobiasz.view.MainContext;

public class MainController {
    private final SignalRepository signalRepo;

    @FXML
    public TabPane main_tabpane;

    private final ObservableList<Signal> signals = FXCollections.observableArrayList();
    private final BooleanProperty selectedSignalSampled = new SimpleBooleanProperty(false);
    private Signal lastOperationResult;

    public MainController(MainContext mainContext) {
        this.signalRepo = mainContext.signalRepository();

        // Replacing repo inner list to make data binding possible.
        this.signalRepo.setBackingList(signals);
    }

//    private void setupDropdownContents(){
//
//        signal_selector1.setItems(signals);
//        signal_selector2.setItems(signals);
//
//        operation_type.getSelectionModel().select(0);
//    }



//    private void setupPaneSwitchGraphRedrawTrigger(){
//        if (main_tabpane != null) {
//            main_tabpane.getSelectionModel().selectedItemProperty().addListener(
//                    (obs, oldTab, newTab) -> redrawCharts()
//            );
//        }
//    }



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
//        setupDropdownContents();
//        setupGraphSourceListeners();
//        setupFrequencyPeriodBinding();
//        setupSignalTypeSelector();
//        setupPaneSwitchGraphRedrawTrigger();
//        setupControlsInteractions();
        setupEscapeKeyFocusReset();
//        setupHistogramBinSliders();
//        setupParameterUpdateOnSignalChange();
//
//        updateControlStates(signal_type.getValue());
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
//    private void redrawCharts() {
//        Signal editorSignal = signal_selector != null ? signal_selector.getValue() : null;
//        drawSignal(editorSignal, signal_chart, signal_bar_chart);
//
//        redrawOperationPreviewCharts();
//
//        drawSignal(lastOperationResult, result_signal_chart, result_signal_barchart);
//    }
//
//    private void redrawOperationPreviewCharts() {
//        Signal operationSignal1 = signal_selector1 != null ? signal_selector1.getValue() : null;
//        drawSignal(operationSignal1, signal_chart1, null);
//
//        Signal operationSignal2 = signal_selector2 != null ? signal_selector2.getValue() : null;
//        drawSignal(operationSignal2, signal_chart2, null);
//    }
//

//
//    @FXML
//    private void handleCalculateOperation() {
//        Signal s1 = signal_selector1 != null ? signal_selector1.getValue() : null;
//        Signal s2 = signal_selector2 != null ? signal_selector2.getValue() : null;
//        String op = operation_type != null ? operation_type.getValue() : null;
//        String resName = result_signal_name != null ? result_signal_name.getText() : null;
//
//        if (s1 == null || s2 == null || op == null) {
//            showError("Błąd kalkulacji", "Upewnij się, że oba sygnały oraz rodzaj operacji zostały wybrane.");
//            return;
//        }
//
//        if (resName == null || resName.trim().isEmpty()) {
//            showError("Brak nazwy", "Sygnał wynikowy musi posiadać odpowiednią nazwę.");
//            return;
//        }
//
//        try {
//            Signal result = null;
//            int skippedDivisionSamples = 0;
//            Signal previouslySelectedEditorSignal = signal_selector != null ? signal_selector.getValue() : null;
//            switch (op) {
//                case "Dodawanie":
//                    result = s1.add(resName, s2);
//                    break;
//                case "Odejmowanie":
//                    result = s1.subtract(resName, s2);
//                    break;
//                case "Mnożenie":
//                    result = s1.multiply(resName, s2);
//                    break;
//                case "Dzielenie":
//                    skippedDivisionSamples = s1.countDivisionSkippedSamples(s2);
//                    result = s1.divide(resName, s2);
//                    break;
//            }
//
//            if (result != null) {
//                Signal existingSignal = signals.stream()
//                        .filter(s -> s.getName().equals(resName))
//                        .findFirst()
//                        .orElse(null);
//
//                boolean selMainMatch = (signal_selector != null && signal_selector.getValue() == existingSignal);
//                boolean sel1Match = (signal_selector1 != null && signal_selector1.getValue() == existingSignal);
//                boolean sel2Match = (signal_selector2 != null && signal_selector2.getValue() == existingSignal);
//
//                if (existingSignal != null) {
//                    int existingSignalIndex = signals.indexOf(existingSignal);
//                    if (existingSignalIndex >= 0) {
//                        signals.set(existingSignalIndex, result);
//                    } else {
//                        signalRepo.addSignal(result);
//                    }
//                } else {
//                    signalRepo.addSignal(result);
//                }
//
//                if (selMainMatch && signal_selector != null) {
//                    signal_selector.getSelectionModel().select(result);
//                } else if (signal_selector != null && previouslySelectedEditorSignal != null && signals.contains(previouslySelectedEditorSignal)) {
//                    signal_selector.getSelectionModel().select(previouslySelectedEditorSignal);
//                }
//
//                if (sel1Match && signal_selector1 != null) {
//                    signal_selector1.getSelectionModel().select(result);
//                }
//                if (sel2Match && signal_selector2 != null) {
//                    signal_selector2.getSelectionModel().select(result);
//                }
//
//                lastOperationResult = result;
//
//                // Upewniamy się, że podglądy starych sygnałów zostaną przerysowane tak jak proszono
//                redrawCharts();
//
//                if ("Dzielenie".equals(op) && skippedDivisionSamples > 0) {
//                    showError(
//                            "Ostrzeżenie dzielenia",
//                            "Niektóre próbki zostały zastąpione zerem, ponieważ wartość mianownika była mniejsza niż epsilon. " +
//                                    "Liczba takich próbek: " + skippedDivisionSamples + "."
//                    );
//
//                    // Ensure charts are repainted after modal dialog is closed.
//                    drawSignal(result, result_signal_chart, result_signal_barchart);
//                }
//
//            }
//        } catch (Exception e) {
//            showError("Błąd kalkulacji sygnałów", e.getMessage());
//        }
//    }



    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Napotkano błąd");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
