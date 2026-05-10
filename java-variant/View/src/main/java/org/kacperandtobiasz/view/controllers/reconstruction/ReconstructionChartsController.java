package org.kacperandtobiasz.view.controllers.reconstruction;

import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import org.kacperandtobiasz.view.MainContext;

public class ReconstructionChartsController {

    private final MainContext mainContext;

    @FXML
    public ScatterChart<Number, Number> quantizationChart;
    @FXML
    public ScatterChart<Number, Number> reconstructionChart;

    public ReconstructionChartsController(MainContext mainContext) {
        this.mainContext = mainContext;
    }

    @FXML
    private void initialize() {
        mainContext.graphService().addQuantizationChart(quantizationChart);
        mainContext.graphService().addReconstructionChart(reconstructionChart);
    }
}
