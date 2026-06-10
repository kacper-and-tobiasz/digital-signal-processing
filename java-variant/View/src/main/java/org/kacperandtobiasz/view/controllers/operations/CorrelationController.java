package org.kacperandtobiasz.view.controllers.operations;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.kacperandtobiasz.model.base.signal.DiscreteSignal;
import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.model.util.SignalProcessingUtil;
import org.kacperandtobiasz.view.MainContext;
import org.kacperandtobiasz.view.SignalSelectionState;
import org.kacperandtobiasz.view.services.GraphService;
import org.kacperandtobiasz.view.utils.AlertUtil;

public class CorrelationController {
    private final MainContext mainContext;
    private final SignalSelectionState signalSelectionState;
    private final GraphService graphService;

    @FXML
    public ComboBox<Signal> firstSignalSelectorComboBox;
    @FXML
    public ComboBox<Signal> secondSignalSelectorComboBox;
    @FXML
    public ComboBox<Signal> resultSignalSelectorComboBox;
    @FXML
    public ComboBox<String> correlationMethodComboBox;
    @FXML
    public Button calculateButton;

    public CorrelationController(MainContext mainContext) {
        this.mainContext = mainContext;
        this.signalSelectionState = mainContext.signalSelectionState();
        this.graphService = mainContext.graphService();
    }

    @FXML
    private void initialize() {
        if (mainContext.signalRepository().getSignals() instanceof ObservableList<Signal> observableSignals) {
            firstSignalSelectorComboBox.setItems(observableSignals);
            secondSignalSelectorComboBox.setItems(observableSignals);
            resultSignalSelectorComboBox.setItems(observableSignals);
        }

        correlationMethodComboBox.getItems().setAll("Bezpośrednia", "Z użyciem splotu");
        correlationMethodComboBox.getSelectionModel().selectFirst();

        if (signalSelectionState.getSelectedSignal() != null) {
            resultSignalSelectorComboBox.setValue(signalSelectionState.getSelectedSignal());
        }

        calculateButton.disableProperty().bind(
                Bindings.createBooleanBinding(() -> {
                    boolean missingFirst = firstSignalSelectorComboBox.getValue() == null;
                    boolean missingSecond = secondSignalSelectorComboBox.getValue() == null;
                    boolean missingResult = resultSignalSelectorComboBox.getValue() == null;
                    boolean missingMethod = correlationMethodComboBox.getValue() == null;
                    return missingFirst || missingSecond || missingResult || missingMethod;
                }, firstSignalSelectorComboBox.valueProperty(), secondSignalSelectorComboBox.valueProperty(), resultSignalSelectorComboBox.valueProperty(), correlationMethodComboBox.valueProperty())
        );
    }

    @FXML
    private void handleCalculateCorrelation() {
        Signal first = firstSignalSelectorComboBox.getValue();
        Signal second = secondSignalSelectorComboBox.getValue();
        Signal result = resultSignalSelectorComboBox.getValue();
        if (first == null || second == null || result == null) {
            AlertUtil.showError("Błąd korelacji", "Wybierz oba sygnały wejściowe oraz sygnał wynikowy.");
            return;
        }
        if (!first.isSampled() || !second.isSampled()) {
            AlertUtil.showError("Błąd korelacji", "Oba sygnały wejściowe muszą być spróbkowane.");
            return;
        }

        try {
            DiscreteSignal correlation = "Z użyciem splotu".equals(correlationMethodComboBox.getValue())
                    ? SignalProcessingUtil.correlateUsingConvolution(first.getDiscreteSignal(), second.getDiscreteSignal())
                    : SignalProcessingUtil.correlateDirect(first.getDiscreteSignal(), second.getDiscreteSignal());

            result.setDiscreteSignal(correlation);

            signalSelectionState.setSelectedSignal(result);
            graphService.drawResultSignalGraphs(result);
        } catch (Exception e) {
            AlertUtil.showError("Błąd korelacji", e.getMessage());
        }
    }
}
