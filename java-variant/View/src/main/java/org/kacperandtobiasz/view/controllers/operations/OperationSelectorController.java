package org.kacperandtobiasz.view.controllers.operations;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.view.MainContext;
import org.kacperandtobiasz.view.services.GraphService;

public class OperationSelectorController {

    private final MainContext mainContext;
    private final GraphService graphService;

    @FXML
    public ComboBox<Signal> signal_selector1;
    @FXML
    public ComboBox<Signal> signal_selector2;
    @FXML
    public TextField result_signal_name;
    @FXML
    public ComboBox<String> operation_type;
    @FXML
    public ScatterChart<Number, Number> signal_chart1;
    @FXML
    public ScatterChart<Number, Number> signal_chart2;

    @FXML
    public Button calcuate_button;

    public OperationSelectorController(MainContext mainContext) {
        this.mainContext = mainContext;
        this.graphService = mainContext.graphService();
    }

    @FXML
    private void initialize() {
        setupGraphSourceListeners();
    }

    private void setupGraphSourceListeners(){
        signal_selector1.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            graphService.drawScatterChart(newVal.getDiscreteSignal(), signal_chart1);
        });

        signal_selector2.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            graphService.drawScatterChart(newVal.getDiscreteSignal(), signal_chart2);
        });
    }

    @FXML
    private void handleCalculateOperation() {
        //fill
    }
}
