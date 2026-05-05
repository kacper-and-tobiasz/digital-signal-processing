package org.kacperandtobiasz.view.controllers.editor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import org.kacperandtobiasz.model.base.signal.Signal;
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

    private void updateStatistics(Signal signal) {
        if (stat_mean == null || stat_abs_mean == null || stat_avg_power == null || stat_rms == null || stat_variance == null) {
            return;
        }

        if (signal == null || !signal.isSampled()) {
            stat_mean.setText("-");
            stat_abs_mean.setText("-");
            stat_avg_power.setText("-");
            stat_rms.setText("-");
            stat_variance.setText("-");
            return;
        }

        stat_mean.setText(String.format("%.4f", signal.mean()));
        stat_abs_mean.setText(String.format("%.4f", signal.absoluteMean()));
        stat_avg_power.setText(String.format("%.4f", signal.averagePower()));
        stat_rms.setText(String.format("%.4f", signal.rms()));
        stat_variance.setText(String.format("%.4f", signal.variance()));
    }
}
