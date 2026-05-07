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
import org.kacperandtobiasz.model.base.signal.generator.GeneratorFactory;
import org.kacperandtobiasz.model.base.signal.generator.SignalGenerator;
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
    }

    @FXML
    private void handleGenerateSignal() {
        Signal targetSignal = signalSelection.getSelectedSignal();
        if (targetSignal == null) return;

        SignalType type = signalTypeComboBox.getValue();
        if (type == null) {
            throw new IllegalArgumentException("Signal type must be selected");
        }

        double samplingRate = samplingRateSpinner.getValue();

        String name = targetSignal.getName();
        SignalParameters params = new SignalParameters(
                amplitudeSpinner.getValue(),
                startTimeSpinner.getValue(),
                durationSpinner.getValue()
        )
                .withPeriod(basePeriodSpinner.getValue())
                .withDutyCycle(dutyCycleSpinner.getValue())
                .withJumpTime(jumpTimeSpinner.getValue())
                .withProbability(probabilitySpinner.getValue())
                .withFirstSample(firstSampleSpinner.getValue())
                .withJumpSample(jumpSampleSpinner.getValue())
                .withSampleLength(sampleLengthSpinner.getValue());

        SignalGenerator newGenerator = GeneratorFactory.create(type, samplingRate, params);
        targetSignal.setGenerator(newGenerator);
        targetSignal.setSamplingFrequency(samplingRate);
        targetSignal.sample();

        mainContext.graphService().drawResultSignalGraphs(targetSignal);
    }


    private void updateControlStates(SignalType type) {
        if (type == null) return;

        boolean usesPeriod = type == SignalType.SIN || type == SignalType.SIN_HALF_RECT ||
            type == SignalType.SIN_FULL_RECT || type == SignalType.RECT ||
            type == SignalType.RECT_SYMMETRIC || type == SignalType.TRIAN;

        boolean usesDutyCycle = type == SignalType.RECT || type == SignalType.RECT_SYMMETRIC || type == SignalType.TRIAN;
        boolean usesJumpTime = type == SignalType.UNIT_JUMP;
        boolean usesProbability = type == SignalType.IMPULSE_NOISE;
        boolean usesDiscreteParams = type == SignalType.UNIT_IMPULSE || type == SignalType.IMPULSE_NOISE;
        boolean usesContinuousParams = !usesDiscreteParams;

        basePeriodSpinner.setDisable(!usesPeriod);
        frequencySpinner.setDisable(!usesPeriod);
        dutyCycleSpinner.setDisable(!usesDutyCycle);
        if (jumpTimeSpinner != null) jumpTimeSpinner.setDisable(!usesJumpTime);
        if (probabilitySpinner != null) probabilitySpinner.setDisable(!usesProbability);

        if (startTimeSpinner != null) startTimeSpinner.setDisable(!usesContinuousParams);
        if (durationSpinner != null) durationSpinner.setDisable(!usesContinuousParams);

        if (firstSampleSpinner != null) firstSampleSpinner.setDisable(!usesDiscreteParams);
        if (jumpSampleSpinner != null) jumpSampleSpinner.setDisable(type != SignalType.UNIT_IMPULSE);
        if (sampleLengthSpinner != null) sampleLengthSpinner.setDisable(!usesDiscreteParams);
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
