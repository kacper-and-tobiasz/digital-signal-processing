package org.kacperandtobiasz.view.controllers.editor;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.view.MainContext;

public class GraphSettingsController {
    @FXML
    public Slider histogram_bins_slider;

    public GraphSettingsController(MainContext mainContext) {

    }

    @FXML
    private void initialize() {
        setupHistogramBinSlider();
    }

    private void setupHistogramBinSlider(){
        if (histogram_bins_slider != null) {
            histogram_bins_slider.valueProperty().addListener((obs, oldVal, newVal) -> {
                Signal currentSignal = signal_selector.getSelectionModel().getSelectedItem();
                if (currentSignal != null && currentSignal.isSampled()) {
                    drawSignal(currentSignal, signal_chart, signal_bar_chart);
                }
            });
        }
    }
}
