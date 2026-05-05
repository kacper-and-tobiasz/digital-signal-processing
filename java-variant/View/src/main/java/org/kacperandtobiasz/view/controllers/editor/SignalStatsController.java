package org.kacperandtobiasz.view.controllers.editor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import org.kacperandtobiasz.view.MainContext;

public class SignalStatsController {
    @FXML
    public Label stat_mean;
    @FXML
    public Label stat_abs_mean;
    @FXML
    public Label stat_avg_power;
    @FXML
    public Label stat_rms;
    @FXML
    public Label stat_variance;

    public SignalStatsController(MainContext mainContext) {
    }
}
