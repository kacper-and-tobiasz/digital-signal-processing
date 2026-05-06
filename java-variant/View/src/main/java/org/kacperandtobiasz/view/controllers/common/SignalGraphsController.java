package org.kacperandtobiasz.view.controllers.common;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.ScatterChart;
import org.kacperandtobiasz.view.MainContext;

public class SignalGraphsController {
    private final MainContext mainContext;

    @FXML
    public ScatterChart<Number, Number> resultScatterChart;
    @FXML
    public BarChart<Number, Number> resultBarChart;

    public SignalGraphsController(MainContext mainContext) {
       this.mainContext = mainContext;
    }

    @FXML
    private void initialize(){
        mainContext.graphService().addResultBarChart(resultBarChart);
        mainContext.graphService().addResultScatterChart(resultScatterChart);
    }
    
}
