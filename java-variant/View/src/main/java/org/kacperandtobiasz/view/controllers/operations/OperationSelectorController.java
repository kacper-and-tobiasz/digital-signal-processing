package org.kacperandtobiasz.view.controllers.operations;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.view.MainContext;
import org.kacperandtobiasz.view.services.GraphService;

public class OperationSelectorController {

    private final MainContext mainContext;
    private final GraphService graphService;

    @FXML
    public ComboBox<Signal> firstSignalSelectorComboBox;
    @FXML
    public ComboBox<Signal> secondSignalSelectorCombobox;
    @FXML
    public TextField result_signal_name;
    @FXML
    public ComboBox<String> operation_type;
    @FXML
    public ScatterChart<Number, Number> firstSignalPreviewChart;
    @FXML
    public ScatterChart<Number, Number> secondSignalPreviewChart;

    @FXML
    public Button calculateButton;

    public OperationSelectorController(MainContext mainContext) {
        this.mainContext = mainContext;
        this.graphService = mainContext.graphService();
    }

    @FXML
    private void initialize() {
        setupGraphSourceListeners();
        setupControlsInteractions();
    }

    private void setupGraphSourceListeners(){
        firstSignalSelectorComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            graphService.drawScatterChart(newVal.getDiscreteSignal(), firstSignalPreviewChart);
        });

        secondSignalSelectorCombobox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            graphService.drawScatterChart(newVal.getDiscreteSignal(), secondSignalPreviewChart);
        });
    }

    private void setupControlsInteractions(){
        if (calculateButton != null) {
            calculateButton.disableProperty().bind(
                    Bindings.createBooleanBinding(() -> {
                        String text = result_signal_name != null ? result_signal_name.getText() : "";
                        boolean noNames = text == null || text.trim().isEmpty();
                        boolean noSignal1 = (firstSignalSelectorComboBox == null || firstSignalSelectorComboBox.getValue() == null);
                        boolean noSignal2 = (secondSignalSelectorCombobox == null || secondSignalSelectorCombobox.getValue() == null);

                        boolean notSampled = false;
                        if (!noSignal1 && !noSignal2) {
                            notSampled = !firstSignalSelectorComboBox.getValue().isSampled() || !secondSignalSelectorCombobox.getValue().isSampled();
                        }

                        return noNames || noSignal1 || noSignal2 || notSampled;
                    }, result_signal_name.textProperty(), firstSignalSelectorComboBox.valueProperty(), secondSignalSelectorCombobox.valueProperty())
            );
        }
    }

    @FXML
    private void handleCalculateOperation() {
        //fill
    }
}
