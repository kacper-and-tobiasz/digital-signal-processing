package org.kacperandtobiasz.view.controllers.operations;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import org.kacperandtobiasz.model.base.signal.Signal;

public class OperationSelectorController {

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

    public OperationSelectorController() {
    }

    private void setupGraphSourceListeners(){
        signal_selector1.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            drawSignal(newVal, signal_chart1, null);
        });

        signal_selector2.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            drawSignal(newVal, signal_chart2, null);
        });
    }
}
