package org.kacperandtobiasz.view.controllers.reconstruction;

import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import org.kacperandtobiasz.view.MainContext;

public class ReconstructionChartsController {

    private final MainContext mainContext;

    @FXML
    public ScatterChart<Number, Number> unquantizedReconstructionChart;
    @FXML
    public ScatterChart<Number, Number> unquantizedNoiseChart;
    @FXML
    public ScatterChart<Number, Number> quantizedReconstructionChart;
    @FXML
    public ScatterChart<Number, Number> quantizedNoiseChart;

    public ReconstructionChartsController(MainContext mainContext) {
        this.mainContext = mainContext;
    }

    @FXML
    private void initialize() {
        mainContext.graphService().setUnquantizedReconstructionChart(unquantizedReconstructionChart);
        mainContext.graphService().setUnquantizedNoiseChart(unquantizedNoiseChart);
        mainContext.graphService().setQuantizedReconstructionChart(quantizedReconstructionChart);
        mainContext.graphService().setQuantizedNoiseChart(quantizedNoiseChart);
    }
}
