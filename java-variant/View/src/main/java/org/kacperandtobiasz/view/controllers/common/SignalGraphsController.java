package org.kacperandtobiasz.view.controllers.common;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.model.base.signal.SignalType;
import org.kacperandtobiasz.view.MainContext;

public class SignalGraphsController {
    @FXML
    public ScatterChart<Number, Number> signal_chart;
    @FXML
    public BarChart signal_bar_chart;

    public SignalGraphsController(MainContext mainContext) {
    }
    
}
