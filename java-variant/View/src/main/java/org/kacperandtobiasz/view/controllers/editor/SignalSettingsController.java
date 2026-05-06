package org.kacperandtobiasz.view.controllers.editor;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.model.base.signal.SignalFactory;
import org.kacperandtobiasz.model.base.signal.SignalParameters;
import org.kacperandtobiasz.model.base.signal.SignalType;
import org.kacperandtobiasz.view.MainContext;
import org.kacperandtobiasz.view.SignalSelectionState;

public class SignalSettingsController {
    @FXML
    public Button generateButton;
    @FXML
    public ComboBox<SignalType> signalTypeComboBox;
    @FXML
    public Spinner<Double> startTimeSpinner;
    @FXML
    public Spinner<Double> durationSpinner;
    @FXML
    public Spinner<Double> amplitudeSpinner;
    @FXML
    public Spinner<Double> samplingRateSpinner;
    @FXML
    public Spinner<Double> basePeriodSpinner;
    @FXML
    public Spinner<Double> dutyCycleSpinner;
    @FXML
    public Spinner<Double> frequencySpinner;
    @FXML
    public Spinner<Double> jumpTimeSpinner;
    @FXML
    public Spinner<Double> probabilitySpinner;
    @FXML
    public Spinner<Integer> firstSampleSpinner;
    @FXML
    public Spinner<Integer> jumpSampleSpinner;
    @FXML
    public Spinner<Integer> sampleLengthSpinner;

    @FXML
    public GridPane generalSignalSettingsPane;
    @FXML
    public VBox specificSignalSettingsVBox;

    private final SignalSelectionState signalSelection;
    private final MainContext mainContext;

    public SignalSettingsController(MainContext mainContext) {
        this.mainContext = mainContext;
        this.signalSelection = mainContext.signalSelectionState();
    }

    @FXML
    private void initialize() {
        setupSignalTypeSelector();
        setupFrequencyPeriodBinding();
        bindToContext();
    }

    @FXML
    private void handleGenerateSignal() {
        Signal targetSignal = signalSelection.getSelectedSignal();
        if (targetSignal == null) return;

        SignalType type = signalTypeComboBox.getValue();
        if (type == null) {
            throw new IllegalArgumentException("Signal type must be selected");
        }

        String name = targetSignal.getName();
        double amp = amplitudeSpinner.getValue();
        double start = startTimeSpinner.getValue();
        double dur = durationSpinner.getValue();
        double period = signalParameterState.getBasePeriod();
        double duty = signalParameterState.getDutyCycle();
        double samplingRate = signalParameterState.getSamplingRate();

        double jump = signalParameterState.getJumpTime();
        double prob = signalParameterState.getProbability();

        int firstSamp = signalParameterState.getFirstSample();
        int jumpSamp = signalParameterState.getJumpSample();
        int sampLen = signalParameterState.getSampleLength();

        SignalParameters params = new SignalParameters(
                signalParameterState.getAmplitude(),
                signalParameterState.getSignalStart(),
                dur
        )
                .withPeriod(period)
                .withDutyCycle(duty)
                .withJumpTime(jump)
                .withProbability(prob)
                .withFirstSample(firstSamp)
                .withJumpSample(jumpSamp)
                .withSampleLength(sampLen);

        Signal newComputedState = SignalFactory.create(type, name, samplingRate, params);
        targetSignal.setGenerator(newComputedState.getGenerator());
        targetSignal.setSamplingFrequency(samplingRate);
        targetSignal.sample();

        mainContext.graphService().drawResultSignalGraphs(targetSignal);
    }


    //    private void updateControlStates(SignalType type) {
//        if (type == null) return;
//
//        boolean usesPeriod = type == SignalType.SIN || type == SignalType.SIN_HALF_RECT ||
//                type == SignalType.SIN_FULL_RECT || type == SignalType.RECT ||
//                type == SignalType.RECT_SYMMETRIC || type == SignalType.TRIAN;
//
//        boolean usesDutyCycle = type == SignalType.RECT || type == SignalType.RECT_SYMMETRIC || type == SignalType.TRIAN;
//        boolean usesJumpTime = type == SignalType.UNIT_JUMP;
//        boolean usesProbability = type == SignalType.IMPULSE_NOISE;
//        boolean usesDiscreteParams = type == SignalType.UNIT_IMPULSE || type == SignalType.IMPULSE_NOISE;
//        boolean usesContinuousParams = !usesDiscreteParams;
//
//        base_period.setDisable(!usesPeriod);
//        signal_frequency.setDisable(!usesPeriod);
//        duty_cycle.setDisable(!usesDutyCycle);
//        if (jump_time != null) jump_time.setDisable(!usesJumpTime);
//        if (probability != null) probability.setDisable(!usesProbability);
//
//        if (signal_start != null) signal_start.setDisable(!usesContinuousParams);
//        if (signal_duration != null) signal_duration.setDisable(!usesContinuousParams);
//
//        if (first_sample != null) first_sample.setDisable(!usesDiscreteParams);
//        if (jump_sample != null) jump_sample.setDisable(type != SignalType.UNIT_IMPULSE);
//        if (sample_length != null) sample_length.setDisable(!usesDiscreteParams);
//    }

    private void bindToContext() {
        if (signalTypeComboBox != null) {
            signalTypeComboBox.valueProperty().bindBidirectional(signalParameterState.signalType());
        }

        if (amplitudeSpinner != null && amplitudeSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(amplitudeSpinner.getValueFactory().valueProperty(), signalParameterState.amplitude().asObject());
        }
        if (startTimeSpinner != null && startTimeSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(startTimeSpinner.getValueFactory().valueProperty(), signalParameterState.signalStart().asObject());
        }
        if (durationSpinner != null && durationSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(durationSpinner.getValueFactory().valueProperty(), signalParameterState.signalDuration().asObject());
        }
        if (basePeriodSpinner != null && basePeriodSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(basePeriodSpinner.getValueFactory().valueProperty(), signalParameterState.basePeriod().asObject());
        }
        if (dutyCycleSpinner != null && dutyCycleSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(dutyCycleSpinner.getValueFactory().valueProperty(), signalParameterState.dutyCycle().asObject());
        }
        if (samplingRateSpinner != null && samplingRateSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(samplingRateSpinner.getValueFactory().valueProperty(), signalParameterState.samplingRate().asObject());
        }
        if (jumpTimeSpinner != null && jumpTimeSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(jumpTimeSpinner.getValueFactory().valueProperty(), signalParameterState.jumpTime().asObject());
        }
        if (probabilitySpinner != null && probabilitySpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(probabilitySpinner.getValueFactory().valueProperty(), signalParameterState.probability().asObject());
        }
        if (firstSampleSpinner != null && firstSampleSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(firstSampleSpinner.getValueFactory().valueProperty(), signalParameterState.firstSample().asObject());
        }
        if (jumpSampleSpinner != null && jumpSampleSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(jumpSampleSpinner.getValueFactory().valueProperty(), signalParameterState.jumpSample().asObject());
        }
        if (sampleLengthSpinner != null && sampleLengthSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(sampleLengthSpinner.getValueFactory().valueProperty(), signalParameterState.sampleLength().asObject());
        }
    }

    private void setupSignalTypeSelector(){
        signalTypeComboBox.getItems().addAll(SignalType.values());
        signalTypeComboBox.getSelectionModel().select(SignalType.SIN);
    }

    private void setupFrequencyPeriodBinding(){
        basePeriodSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal >= 0.01) {
                double expectedFreq = 1.0 / newVal;
                if (expectedFreq < 0.01) expectedFreq = 0.01;
                if (frequencySpinner.getValue() == null || Math.abs(frequencySpinner.getValue() - expectedFreq) > 1e-6) {
                    frequencySpinner.getValueFactory().setValue(expectedFreq);
                }
            } else if (newVal != null && newVal < 0.01) {
                basePeriodSpinner.getValueFactory().setValue(0.01);
            }
        });

        frequencySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal >= 0.01) {
                double expectedPeriod = 1.0 / newVal;
                if (expectedPeriod < 0.01) expectedPeriod = 0.01;
                if (basePeriodSpinner.getValue() == null || Math.abs(basePeriodSpinner.getValue() - expectedPeriod) > 1e-6) {
                    basePeriodSpinner.getValueFactory().setValue(expectedPeriod);
                }
            } else if (newVal != null && newVal < 0.01) {
                frequencySpinner.getValueFactory().setValue(0.01);
            }
        });
    }
}
