package org.kacperandtobiasz.view.controllers.reconstruction;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.kacperandtobiasz.model.base.signal.DiscreteSignal;
import org.kacperandtobiasz.model.base.signal.QuantizedRoundedSignal;
import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.model.base.signal.metrics.SignalMetrics;
import org.kacperandtobiasz.model.base.signal.reconstruction.Reconstructor;
import org.kacperandtobiasz.model.base.signal.reconstruction.SincReconstructor;
import org.kacperandtobiasz.model.base.signal.reconstruction.ZeroOrderHoldReconstructor;
import org.kacperandtobiasz.view.MainContext;
import org.kacperandtobiasz.view.SignalSelectionState;
import org.kacperandtobiasz.view.services.GraphService;
import org.kacperandtobiasz.view.utils.AlertUtil;

public class ReconstructionSettingsController {

    private final MainContext mainContext;
    private final GraphService graphService;
    private final SignalSelectionState signalSelectionState;
    private final BooleanProperty quantizedFlag = new SimpleBooleanProperty(false);

    @FXML
    public ComboBox<Signal> signalSelectorComboBox;
    @FXML
    public Spinner<Integer> bitsSpinner;
    @FXML
    public Button quantizeButton;
    @FXML
    public ComboBox<String> reconstructionMethodComboBox;
    @FXML
    public Button reconstructButton;
    @FXML
    public Label mseLabel;
    @FXML
    public Label snrLabel;
    @FXML
    public Label psnrLabel;
    @FXML
    public Label mdLabel;
    @FXML
    public Label enobLabel;
    @FXML
    public Label enobTheoreticalLabel;

    @FXML
    public Label unquantizedMseLabel;
    @FXML
    public Label unquantizedSnrLabel;
    @FXML
    public Label unquantizedPsnrLabel;
    @FXML
    public Label unquantizedMdLabel;
    @FXML
    public Label unquantizedEnobLabel;

    public ReconstructionSettingsController(MainContext mainContext) {
        this.mainContext = mainContext;
        this.graphService = mainContext.graphService();
        this.signalSelectionState = mainContext.signalSelectionState();
    }

    @FXML
    private void initialize() {
        bitsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 32, 8));

        if (mainContext.signalRepository().getSignals() instanceof ObservableList<Signal> signals) {
            signalSelectorComboBox.setItems(signals);
        }

        signalSelectorComboBox.valueProperty().bindBidirectional(signalSelectionState.selectedSignal());

        signalSelectionState.selectedSignal().addListener((obs, oldVal, newVal) -> {
            quantizedFlag.set(newVal != null && newVal.isQuantized());
            updateChartsForSelectedSignal(newVal);
            refreshButtonStates();
        });

        mainContext.graphService().addGraphDrawListener(event -> {
            Signal currentSignal = signalSelectorComboBox.getValue();
            if (currentSignal != null && currentSignal.equals(event.getSignal())) {
                updateChartsForSelectedSignal(currentSignal);
                refreshButtonStates();
            }
        });

        //just to be safe
        reconstructionMethodComboBox.valueProperty().addListener((obs, oldVal, newVal) -> refreshButtonStates());

        updateChartsForSelectedSignal(signalSelectorComboBox.getValue());
        refreshButtonStates();
    }

    private void refreshButtonStates() {
        Signal selected = signalSelectorComboBox.getValue();
        quantizeButton.setDisable(selected == null || !selected.isSampled());

        String method = reconstructionMethodComboBox.getValue();
        reconstructButton.setDisable(!quantizedFlag.get() || method == null);
    }

    private void updateChartsForSelectedSignal(Signal signal) {
        if (signal == null || !signal.isSampled()) {
            graphService.drawReconstructionPhaseCharts(null);
            return;
        }

        graphService.drawReconstructionPhaseCharts(signal);
    }



    @FXML
    private void handleQuantize() {
        Signal signal = signalSelectorComboBox.getValue();
        if (signal == null || !signal.isSampled()) {
            AlertUtil.showError("Błąd kwantyzacji", "Wybrany sygnał nie jest spróbkowany.");
            return;
        }

        try {
            int bits = bitsSpinner.getValue();
            QuantizedRoundedSignal quantized = signal.getDiscreteSignal().quantizeWithRounding(bits);
            signal.setQuantizedSignal(quantized);
            signal.setReconstructedSignal(null);
            signal.setUnquantizedReconstructedSignal(null);
            quantizedFlag.set(true);

            graphService.drawReconstructionPhaseCharts(signal);
            clearMetrics();
            refreshButtonStates();

        } catch (Exception e) {
            AlertUtil.showError("Błąd kwantyzacji", e.getMessage());
        }
    }

    @FXML
    private void handleReconstruct() {
        Signal signal = signalSelectorComboBox.getValue();
        if (signal == null || !signal.isQuantized()) {
            AlertUtil.showError("Błąd rekonstrukcji", "Sygnał nie został skwantyzowany.");
            return;
        }

        String method = reconstructionMethodComboBox.getValue();
        if (method == null) {
            AlertUtil.showError("Błąd rekonstrukcji", "Nie wybrano metody rekonstrukcji.");
            return;
        }

        try {
            Reconstructor reconstructor = switch (method) {
                case "Ekstrapolacja zerowego rzędu" -> new ZeroOrderHoldReconstructor();
                case "Rekonstrukcja sinc" -> new SincReconstructor();
                default -> throw new IllegalArgumentException("Nieznana metoda rekonstrukcji: " + method);
            };

            QuantizedRoundedSignal quantized = signal.getQuantizedSignal();
            double targetFrequency = quantized.getSamplingFrequency() * 50;
            
            DiscreteSignal reconstructed = reconstructor.reconstruct(quantized.toDiscreteSignal(), targetFrequency);
            signal.setReconstructedSignal(reconstructed);

            DiscreteSignal unquantizedDs = signal.getDiscreteSignal();
            DiscreteSignal unquantizedReconstructed = reconstructor.reconstruct(unquantizedDs, targetFrequency);
            signal.setUnquantizedReconstructedSignal(unquantizedReconstructed);

            graphService.drawReconstructionPhaseCharts(signal);
            updateMetrics(signal.getHighProbingFrequencyBaseline(), reconstructed, unquantizedReconstructed);
            refreshButtonStates();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Błąd rekonstrukcji", e.getMessage());
        }
    }

    private void updateMetrics(DiscreteSignal original, DiscreteSignal reconstructed, DiscreteSignal unquantizedReconstructed) {
        try {
            mseLabel.setText(String.format("%.6f", SignalMetrics.calculateMSE(original, reconstructed)));
            snrLabel.setText(String.format("%.4f", SignalMetrics.calculateSNR(original, reconstructed)));
            psnrLabel.setText(String.format("%.4f", SignalMetrics.calculatePSNR(original, reconstructed)));
            mdLabel.setText(String.format("%.6f", SignalMetrics.calculateMD(original, reconstructed)));
            enobLabel.setText(String.format("%.4f", SignalMetrics.calculateENOB(original, reconstructed)));
            enobTheoreticalLabel.setText(String.valueOf(bitsSpinner.getValue()));
            
            unquantizedMseLabel.setText(String.format("%.6f", SignalMetrics.calculateMSE(original, unquantizedReconstructed)));
            unquantizedSnrLabel.setText(String.format("%.4f", SignalMetrics.calculateSNR(original, unquantizedReconstructed)));
            unquantizedPsnrLabel.setText(String.format("%.4f", SignalMetrics.calculatePSNR(original, unquantizedReconstructed)));
            unquantizedMdLabel.setText(String.format("%.6f", SignalMetrics.calculateMD(original, unquantizedReconstructed)));
            unquantizedEnobLabel.setText(String.format("%.4f", SignalMetrics.calculateENOB(original, unquantizedReconstructed)));
        } catch (Exception e) {
            System.err.println("Metrics calculation failed: " + e.getMessage());
            e.printStackTrace();
            clearMetrics();
        }
    }

    private void clearMetrics() {
        mseLabel.setText("-");
        snrLabel.setText("-");
        psnrLabel.setText("-");
        mdLabel.setText("-");
        enobLabel.setText("-");
        enobTheoreticalLabel.setText("-");
        unquantizedMseLabel.setText("-");
        unquantizedSnrLabel.setText("-");
        unquantizedPsnrLabel.setText("-");
        unquantizedMdLabel.setText("-");
        unquantizedEnobLabel.setText("-");
    }
}
