package org.kacperandtobiasz.view.controllers.operations;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.view.MainContext;
import org.kacperandtobiasz.view.services.GraphService;
import org.kacperandtobiasz.view.utils.AlertUtil;

public class OperationSelectorController {

    private final MainContext mainContext;
    private final GraphService graphService;

    @FXML
    public ComboBox<Signal> firstSignalSelectorComboBox;
    @FXML
    public ComboBox<Signal> secondSignalSelectorCombobox;
    @FXML
    public ComboBox<String> operation_type;
    @FXML
    public ScatterChart<Number, Number> firstSignalPreviewChart;
    @FXML
    public ScatterChart<Number, Number> secondSignalPreviewChart;

    @FXML
    public Button calculateButton;

    @FXML
    public ComboBox<Signal> resultSignalSelectorComboBox;

    public OperationSelectorController(MainContext mainContext) {
        this.mainContext = mainContext;
        this.graphService = mainContext.graphService();
    }

    @FXML
    private void initialize() {
        setupGraphSourceListeners();
        setupControlsInteractions();

        if (mainContext.signalRepository().getSignals() instanceof ObservableList<Signal> signals) {
            firstSignalSelectorComboBox.setItems(signals);
            secondSignalSelectorCombobox.setItems(signals);
            resultSignalSelectorComboBox.setItems(signals);
        }
    }

    private void setupGraphSourceListeners(){
        firstSignalSelectorComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            firstSignalPreviewChart.getData().clear();
            if (newVal != null && newVal.hasRealSignal()) {
                graphService.addDataToScatterChart(newVal.getDiscreteSignal(), firstSignalPreviewChart);
            }
        });

        secondSignalSelectorCombobox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            secondSignalPreviewChart.getData().clear();
            if (newVal != null && newVal.hasRealSignal()) {
                graphService.addDataToScatterChart(newVal.getDiscreteSignal(), secondSignalPreviewChart);
            }
        });
    }

    private void setupControlsInteractions(){
        if (calculateButton != null) {
            calculateButton.disableProperty().bind(
                    Bindings.createBooleanBinding(() -> {
                        boolean noResultSignal = (resultSignalSelectorComboBox == null || resultSignalSelectorComboBox.getValue() == null);
                        boolean noSignal1 = (firstSignalSelectorComboBox == null || firstSignalSelectorComboBox.getValue() == null);
                        boolean noSignal2 = (secondSignalSelectorCombobox == null || secondSignalSelectorCombobox.getValue() == null);

                        boolean notSampled = false;
                        if (!noSignal1 && !noSignal2) {
                            notSampled = !firstSignalSelectorComboBox.getValue().hasRealSignal() || !secondSignalSelectorCombobox.getValue().hasRealSignal();
                        }

                        return noResultSignal || noSignal1 || noSignal2 || notSampled;
                    }, resultSignalSelectorComboBox.valueProperty(), firstSignalSelectorComboBox.valueProperty(), secondSignalSelectorCombobox.valueProperty())
            );
        }

        if (operation_type != null && operation_type.getItems().size() > 0) {
            operation_type.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleCalculateOperation() {
        Signal s1 = firstSignalSelectorComboBox != null ? firstSignalSelectorComboBox.getValue() : null;
        Signal s2 = secondSignalSelectorCombobox != null ? secondSignalSelectorCombobox.getValue() : null;
        String op = operation_type != null ? operation_type.getValue() : null;
        Signal result = resultSignalSelectorComboBox != null? resultSignalSelectorComboBox.getValue() : null;

        if (s1 == null || s2 == null || op == null || result == null) {
            AlertUtil.showError("Błąd operacji", "Upewnij się, że wszystkie sygnały oraz rodzaj operacji zostały wybrane.");
            return;
        }

        try {
            int skippedDivisionSamples = 0;

            switch (op) {
                case "Dodawanie":
                    s1.add(s2, result);
                    break;
                case "Odejmowanie":
                    s1.subtract(s2, result);
                    break;
                case "Mnożenie":
                    s1.multiply(s2, result);
                    break;
                case "Dzielenie":
                    skippedDivisionSamples = s1.countDivisionSkippedSamples(s2);
                    s1.divide(s2, result);
                    break;
            }

            mainContext.signalSelectionState().setSelectedSignal(result);

            if ("Dzielenie".equals(op) && skippedDivisionSamples > 0) {
                AlertUtil.showError(
                        "Ostrzeżenie dzielenia",
                        "Niektóre próbki zostały zastąpione zerem, ponieważ wartość mianownika była mniejsza niż epsilon. " +
                                "Liczba takich próbek: " + skippedDivisionSamples + "."
                );
            }

            redrawCharts(s1, s2, result);

        } catch (Exception e) {
            AlertUtil.showError("Błąd kalkulacji sygnałów", e.getMessage());
        }
    }

    private void redrawCharts(Signal s1, Signal s2, Signal result) {
        graphService.drawResultSignalGraphs(result);
        graphService.drawScatterChart(s1, firstSignalPreviewChart);
        graphService.drawScatterChart(s2, secondSignalPreviewChart);
    }
}
