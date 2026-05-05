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
import org.kacperandtobiasz.view.SignalParameterState;
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

    private final SignalParameterState signalParameters;
    private final SignalSelectionState signalSelection;
    private final MainContext mainContext;

    public SignalSettingsController(MainContext mainContext) {
        this.mainContext = mainContext;
        this.signalParameters = mainContext.signalParameters();
        this.signalSelection = mainContext.signalSelection();
    }

    @FXML
    private void initialize() {
        setupSignalTypeSelector();
        bindToContext();
        setupFrequencyPeriodBinding();
    }

    @FXML
    private void handleGenerateSignal() {
        Signal targetSignal = signalSelection.getSelectedSignal();
        if (targetSignal == null) return;

        SignalType type = signalParameters.getSignalType();
        if (type == null) {
            throw new IllegalArgumentException("Signal type must be selected");
        }

        String name = targetSignal.getName();
        double amp = signalParameters.getAmplitude();
        double start = signalParameters.getSignalStart();
        double dur = signalParameters.getSignalDuration();
        double period = signalParameters.getBasePeriod();
        double duty = signalParameters.getDutyCycle();
        double samplingRate = signalParameters.getSamplingRate();

        double jump = signalParameters.getJumpTime();
        double prob = signalParameters.getProbability();

        int firstSamp = signalParameters.getFirstSample();
        int jumpSamp = signalParameters.getJumpSample();
        int sampLen = signalParameters.getSampleLength();

        SignalParameters params = new SignalParameters(amp, start, dur)
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

        mainContext.graphService().setCurrentSignal(targetSignal);
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
            signalTypeComboBox.valueProperty().bindBidirectional(signalParameters.signalType());
        }

        if (amplitudeSpinner != null && amplitudeSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(amplitudeSpinner.getValueFactory().valueProperty(), signalParameters.amplitude().asObject());
        }
        if (startTimeSpinner != null && startTimeSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(startTimeSpinner.getValueFactory().valueProperty(), signalParameters.signalStart().asObject());
        }
        if (durationSpinner != null && durationSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(durationSpinner.getValueFactory().valueProperty(), signalParameters.signalDuration().asObject());
        }
        if (basePeriodSpinner != null && basePeriodSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(basePeriodSpinner.getValueFactory().valueProperty(), signalParameters.basePeriod().asObject());
        }
        if (dutyCycleSpinner != null && dutyCycleSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(dutyCycleSpinner.getValueFactory().valueProperty(), signalParameters.dutyCycle().asObject());
        }
        if (samplingRateSpinner != null && samplingRateSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(samplingRateSpinner.getValueFactory().valueProperty(), signalParameters.samplingRate().asObject());
        }
        if (jumpTimeSpinner != null && jumpTimeSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(jumpTimeSpinner.getValueFactory().valueProperty(), signalParameters.jumpTime().asObject());
        }
        if (probabilitySpinner != null && probabilitySpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(probabilitySpinner.getValueFactory().valueProperty(), signalParameters.probability().asObject());
        }
        if (firstSampleSpinner != null && firstSampleSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(firstSampleSpinner.getValueFactory().valueProperty(), signalParameters.firstSample().asObject());
        }
        if (jumpSampleSpinner != null && jumpSampleSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(jumpSampleSpinner.getValueFactory().valueProperty(), signalParameters.jumpSample().asObject());
        }
        if (sampleLengthSpinner != null && sampleLengthSpinner.getValueFactory() != null) {
            Bindings.bindBidirectional(sampleLengthSpinner.getValueFactory().valueProperty(), signalParameters.sampleLength().asObject());
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
