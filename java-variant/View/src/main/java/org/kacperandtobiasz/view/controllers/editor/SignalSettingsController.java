package org.kacperandtobiasz.view.controllers.editor;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.kacperandtobiasz.model.base.signal.SignalType;
import org.kacperandtobiasz.view.MainContext;
import org.kacperandtobiasz.view.SignalParameterState;

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

    private final SignalParameterState signalParameters;

    public SignalSettingsController(MainContext mainContext) {
        this.signalParameters = mainContext.signalParameters();
    }

    @FXML
    private void initialize() {
        setupSignalTypeSelector();
        bindToContext();
        setupFrequencyPeriodBinding();
    }

    private void bindToContext() {
        if (signal_type != null) {
            signal_type.valueProperty().bindBidirectional(signalParameters.signalType());
        }

        if (amplitude != null && amplitude.getValueFactory() != null) {
            Bindings.bindBidirectional(amplitude.getValueFactory().valueProperty(), signalParameters.amplitude().asObject());
        }
        if (signal_start != null && signal_start.getValueFactory() != null) {
            Bindings.bindBidirectional(signal_start.getValueFactory().valueProperty(), signalParameters.signalStart().asObject());
        }
        if (signal_duration != null && signal_duration.getValueFactory() != null) {
            Bindings.bindBidirectional(signal_duration.getValueFactory().valueProperty(), signalParameters.signalDuration().asObject());
        }
        if (base_period != null && base_period.getValueFactory() != null) {
            Bindings.bindBidirectional(base_period.getValueFactory().valueProperty(), signalParameters.basePeriod().asObject());
        }
        if (duty_cycle != null && duty_cycle.getValueFactory() != null) {
            Bindings.bindBidirectional(duty_cycle.getValueFactory().valueProperty(), signalParameters.dutyCycle().asObject());
        }
        if (sampling_rate != null && sampling_rate.getValueFactory() != null) {
            Bindings.bindBidirectional(sampling_rate.getValueFactory().valueProperty(), signalParameters.samplingRate().asObject());
        }
        if (jump_time != null && jump_time.getValueFactory() != null) {
            Bindings.bindBidirectional(jump_time.getValueFactory().valueProperty(), signalParameters.jumpTime().asObject());
        }
        if (probability != null && probability.getValueFactory() != null) {
            Bindings.bindBidirectional(probability.getValueFactory().valueProperty(), signalParameters.probability().asObject());
        }
        if (first_sample != null && first_sample.getValueFactory() != null) {
            Bindings.bindBidirectional(first_sample.getValueFactory().valueProperty(), signalParameters.firstSample().asObject());
        }
        if (jump_sample != null && jump_sample.getValueFactory() != null) {
            Bindings.bindBidirectional(jump_sample.getValueFactory().valueProperty(), signalParameters.jumpSample().asObject());
        }
        if (sample_length != null && sample_length.getValueFactory() != null) {
            Bindings.bindBidirectional(sample_length.getValueFactory().valueProperty(), signalParameters.sampleLength().asObject());
        }
    }

    private void setupSignalTypeSelector(){
        signal_type.getItems().addAll(SignalType.values());
        signal_type.getSelectionModel().select(SignalType.SIN);
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

    @FXML
    private void handleGenerateSignal(){
        //fill
    }
}
