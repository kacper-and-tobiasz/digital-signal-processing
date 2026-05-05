package org.kacperandtobiasz.view.controllers.editor;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.kacperandtobiasz.model.base.signal.SignalType;
import org.kacperandtobiasz.view.MainContext;

public class SignalSettingsController {
    @FXML
    public Button generate_button;
    @FXML
    public ComboBox<SignalType> signal_type;
    @FXML
    public Spinner<Double> signal_start;
    @FXML
    public Spinner<Double> signal_duration;
    @FXML
    public Spinner<Double> amplitude;
    @FXML
    public Spinner<Double> sampling_rate;
    @FXML
    public Spinner<Double> base_period;
    @FXML
    public Spinner<Double> duty_cycle;
    @FXML
    public Spinner<Double> signal_frequency;
    @FXML
    public Spinner<Double> jump_time;
    @FXML
    public Spinner<Double> probability;
    @FXML
    public Spinner<Integer> first_sample;
    @FXML
    public Spinner<Integer> jump_sample;
    @FXML
    public Spinner<Integer> sample_length;

    @FXML
    public GridPane general_signal_settings;
    @FXML
    public VBox specific_signal_settings;

    public SignalSettingsController(MainContext mainContext) {
    }

    @FXML
    private void initialize() {
        setupFrequencyPeriodBinding();
    }

    private void setupFrequencyPeriodBinding(){
        base_period.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal >= 0.01) {
                double expectedFreq = 1.0 / newVal;
                if (expectedFreq < 0.01) expectedFreq = 0.01;
                if (signal_frequency.getValue() == null || Math.abs(signal_frequency.getValue() - expectedFreq) > 1e-6) {
                    signal_frequency.getValueFactory().setValue(expectedFreq);
                }
            } else if (newVal != null && newVal < 0.01) {
                base_period.getValueFactory().setValue(0.01);
            }
        });

        signal_frequency.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal >= 0.01) {
                double expectedPeriod = 1.0 / newVal;
                if (expectedPeriod < 0.01) expectedPeriod = 0.01;
                if (base_period.getValue() == null || Math.abs(base_period.getValue() - expectedPeriod) > 1e-6) {
                    base_period.getValueFactory().setValue(expectedPeriod);
                }
            } else if (newVal != null && newVal < 0.01) {
                signal_frequency.getValueFactory().setValue(0.01);
            }
        });
    }
}
