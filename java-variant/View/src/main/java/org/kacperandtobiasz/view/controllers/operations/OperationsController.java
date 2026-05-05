package org.kacperandtobiasz.view.controllers.operations;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.model.base.signal.SignalType;
import org.kacperandtobiasz.view.MainContext;

public class OperationsController {

    @FXML
    public Slider result_histogram_bins_slider;

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
    public ScatterChart<Number, Number> result_signal_chart;
    @FXML
    public BarChart result_signal_barchart;
    @FXML
    public Button calcuate_button;


    public OperationsController(MainContext mainContext) {
    }
}
