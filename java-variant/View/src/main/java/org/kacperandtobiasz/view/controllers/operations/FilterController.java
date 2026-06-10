package org.kacperandtobiasz.view.controllers.operations;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.kacperandtobiasz.model.base.signal.DiscreteSignal;
import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.model.util.SignalProcessingUtil;
import org.kacperandtobiasz.model.util.SignalProcessingUtil.FilterType;
import org.kacperandtobiasz.model.util.SignalProcessingUtil.WindowType;
import org.kacperandtobiasz.view.MainContext;
import org.kacperandtobiasz.view.SignalSelectionState;
import org.kacperandtobiasz.view.services.GraphService;
import org.kacperandtobiasz.view.utils.AlertUtil;

public class FilterController {
    private final MainContext mainContext;
    private final SignalSelectionState signalSelectionState;
    private final GraphService graphService;

    @FXML
    public ComboBox<Signal> inputSignalSelectorComboBox;
    @FXML
    public ComboBox<Signal> resultSignalSelectorComboBox;
    @FXML
    public ComboBox<String> filterTypeComboBox;
    @FXML
    public ComboBox<String> windowTypeComboBox;
    @FXML
    public Spinner<Integer> coefficientCountSpinner;
    @FXML
    public Spinner<Double> cutoffFrequencySpinner;
    @FXML
    public Button applyFilterButton;

    public FilterController(MainContext mainContext) {
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

        filterTypeComboBox.getItems().setAll("Dolnoprzepustowy", "Górnoprzepustowy");
        filterTypeComboBox.getSelectionModel().selectFirst();

        windowTypeComboBox.getItems().setAll("Okno prostokątne", "Okno Hamminga");
        windowTypeComboBox.getSelectionModel().selectFirst();

        coefficientCountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 999, 63, 2));
        cutoffFrequencySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0001, 100000.0, 5.0, 0.1));

        if (signalSelectionState.getSelectedSignal() != null) {
            resultSignalSelectorComboBox.setValue(signalSelectionState.getSelectedSignal());
        }

        applyFilterButton.disableProperty().bind(
                Bindings.createBooleanBinding(() -> {
                    boolean missingInput = inputSignalSelectorComboBox.getValue() == null;
                    boolean missingResult = resultSignalSelectorComboBox.getValue() == null;
                    boolean missingType = filterTypeComboBox.getValue() == null;
                    boolean missingWindow = windowTypeComboBox.getValue() == null;
                    return missingInput || missingResult || missingType || missingWindow;
                }, inputSignalSelectorComboBox.valueProperty(), resultSignalSelectorComboBox.valueProperty(), filterTypeComboBox.valueProperty(), windowTypeComboBox.valueProperty())
        );
    }

    @FXML
    private void handleApplyFilter() {
        Signal input = inputSignalSelectorComboBox.getValue();
        Signal result = resultSignalSelectorComboBox.getValue();
        if (input == null || result == null) {
            AlertUtil.showError("Błąd filtracji", "Wybierz sygnał wejściowy oraz wynikowy.");
            return;
        }
        if (!input.isSampled()) {
            AlertUtil.showError("Błąd filtracji", "Sygnał wejściowy musi być spróbkowany.");
            return;
        }

        try {
            int coefficientCount = coefficientCountSpinner.getValue();
            double cutoffFrequency = cutoffFrequencySpinner.getValue();
            double samplingFrequency = input.getSamplingFrequency();

            FilterType filterType = "Górnoprzepustowy".equals(filterTypeComboBox.getValue())
                    ? FilterType.HIGH_PASS
                    : FilterType.LOW_PASS;
            WindowType windowType = "Okno Hamminga".equals(windowTypeComboBox.getValue())
                    ? WindowType.HAMMING
                    : WindowType.RECTANGULAR;

            double[] coefficients = SignalProcessingUtil.designFirFilter(
                    coefficientCount,
                    cutoffFrequency,
                    samplingFrequency,
                    filterType,
                    windowType
            );

            DiscreteSignal filteredSignal = SignalProcessingUtil.applyFirFilter(input.getDiscreteSignal(), coefficients);
            result.setDiscreteSignal(filteredSignal);

            signalSelectionState.setSelectedSignal(result);
            graphService.drawResultSignalGraphs(result);
        } catch (Exception e) {
            AlertUtil.showError("Błąd filtracji", e.getMessage());
        }
    }
}
