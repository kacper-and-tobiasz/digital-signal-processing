package org.kacperandtobiasz.view.controllers.editor;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import org.kacperandtobiasz.view.MainContext;
import org.kacperandtobiasz.view.services.GraphService;

public class GraphSettingsController {
    @FXML
    public Slider histogram_bins_slider;

    private MainContext mainContext;
    private GraphService graphService;

    public GraphSettingsController(MainContext mainContext) {
        this.mainContext = mainContext;
        this.graphService = mainContext.graphService();
    }

    @FXML
    private void initialize() {
        setupHistogramBinSlider();
    }

    private void setupHistogramBinSlider(){
        if (histogram_bins_slider != null) {
            histogram_bins_slider.valueProperty().addListener((obs, oldVal, newVal) -> {
                graphService.setHistogramBinCount(newVal.intValue());
            });
        }
    }
}
