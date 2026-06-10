package org.kacperandtobiasz.view.controllers.operations;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.kacperandtobiasz.model.base.signal.DiscreteSignal;
import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.model.base.signal.TransformResult;
import org.kacperandtobiasz.model.util.SignalProcessingUtil;
import org.kacperandtobiasz.model.util.SignalProcessingUtil.DctMethod;
import org.kacperandtobiasz.model.util.SignalProcessingUtil.FourierMethod;
import org.kacperandtobiasz.view.MainContext;
import org.kacperandtobiasz.view.SignalSelectionState;
import org.kacperandtobiasz.view.services.GraphService;
import org.kacperandtobiasz.view.utils.AlertUtil;

public class TransformController {
    private final MainContext mainContext;
    private final SignalSelectionState signalSelectionState;
    private final GraphService graphService;

    @FXML
    public ComboBox<Signal> inputSignalSelectorComboBox;
    @FXML
    public ComboBox<Signal> resultSignalSelectorComboBox;
    @FXML
    public ComboBox<String> transformTypeComboBox;
    @FXML
    public ComboBox<String> methodComboBox;
    @FXML
    public ComboBox<String> complexViewModeComboBox;
    @FXML
    public Button calculateButton;
    @FXML
    public Button createS1Button;
    @FXML
    public Label elapsedTimeLabel;

    public TransformController(MainContext mainContext) {
        this.mainContext = mainContext;
        this.signalSelectionState = mainContext.signalSelectionState();
        this.graphService = mainContext.graphService();
    }

    @FXML
    private void initialize() {
        if (mainContext.signalRepository().getSignals() instanceof ObservableList<Signal> observableSignals) {
            inputSignalSelectorComboBox.setItems(observableSignals);
            resultSignalSelectorComboBox.setItems(observableSignals);
        }

        transformTypeComboBox.getItems().setAll("Fourier", "DCT II");
        transformTypeComboBox.getSelectionModel().selectFirst();
        complexViewModeComboBox.getItems().setAll("W1", "W2");
        complexViewModeComboBox.getSelectionModel().selectFirst();

        transformTypeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> setupMethods());
        complexViewModeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            graphService.setComplexViewMode(newValue);
            graphService.drawResultSignalGraphs(signalSelectionState.getSelectedSignal());
        });
        setupMethods();

        if (signalSelectionState.getSelectedSignal() != null) {
            resultSignalSelectorComboBox.setValue(signalSelectionState.getSelectedSignal());
        }

        calculateButton.disableProperty().bind(
                Bindings.createBooleanBinding(() ->
                                inputSignalSelectorComboBox.getValue() == null
                                        || resultSignalSelectorComboBox.getValue() == null
                                        || transformTypeComboBox.getValue() == null
                                        || methodComboBox.getValue() == null,
                        inputSignalSelectorComboBox.valueProperty(),
                        resultSignalSelectorComboBox.valueProperty(),
                        transformTypeComboBox.valueProperty(),
                        methodComboBox.valueProperty())
        );
    }

    @FXML
    private void handleCalculateTransform() {
        Signal input = inputSignalSelectorComboBox.getValue();
        Signal result = resultSignalSelectorComboBox.getValue();
        if (input == null || result == null) {
            AlertUtil.showError("Blad transformacji", "Wybierz sygnal wejsciowy oraz wynikowy.");
            return;
        }
        if (!input.hasRealSignal()) {
            AlertUtil.showError("Blad transformacji", "Sygnal wejsciowy musi miec wartosci rzeczywiste.");
            return;
        }

        try {
            TransformResult transformResult;
            if ("DCT II".equals(transformTypeComboBox.getValue())) {
                DctMethod method = "FCT II".equals(methodComboBox.getValue()) ? DctMethod.FAST : DctMethod.DEFINITION;
                transformResult = SignalProcessingUtil.transformDctII(input.getDiscreteSignal(), method);
                result.setDiscreteSignal(transformResult.realSignal());
            } else {
                FourierMethod method = "FFT DIF".equals(methodComboBox.getValue()) ? FourierMethod.FFT_DIF : FourierMethod.DEFINITION;
                transformResult = SignalProcessingUtil.transformFourier(input.getDiscreteSignal(), method);
                result.setComplexSignal(transformResult.complexSignal());
            }

            graphService.setComplexViewMode(complexViewModeComboBox.getValue());
            elapsedTimeLabel.setText(String.format("Czas: %.3f ms", transformResult.elapsedNanos() / 1_000_000.0));
            signalSelectionState.setSelectedSignal(result);
            graphService.drawResultSignalGraphs(result);
        } catch (Exception e) {
            AlertUtil.showError("Blad transformacji", e.getMessage());
        }
    }

    @FXML
    private void handleCreateS1Signal() {
        try {
            String name = "S1";
            String candidate = name;
            int counter = 1;
            while (!mainContext.signalRepository().isSignalNameAvailable(candidate)) {
                candidate = name + "(" + counter + ")";
                counter++;
            }

            DiscreteSignal s1 = SignalProcessingUtil.createS1Signal();
            Signal signal = new Signal(candidate, s1);
            mainContext.signalRepository().addSignal(signal);
            inputSignalSelectorComboBox.getSelectionModel().select(signal);
            signalSelectionState.setSelectedSignal(signal);
            graphService.drawResultSignalGraphs(signal);
        } catch (Exception e) {
            AlertUtil.showError("Błąd tworzenia S1", e.getMessage());
        }
    }

    private void setupMethods() {
        if ("DCT II".equals(transformTypeComboBox.getValue())) {
            methodComboBox.getItems().setAll("Definicja", "FCT II");
            complexViewModeComboBox.setDisable(true);
        } else {
            methodComboBox.getItems().setAll("Definicja", "FFT DIF");
            complexViewModeComboBox.setDisable(false);
        }
        methodComboBox.getSelectionModel().selectFirst();
    }
}
