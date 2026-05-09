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
        setupSignalSettingsAvailability();
        setupParameterUpdateOnSignalChange();
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

        signalTypeComboBox.valueProperty().addListener(
                (obs, oldVal, newVal) ->
                        updateControlStates(newVal)
        );


        updateControlStates(signalTypeComboBox.getValue());
    }

    private void setupFrequencyPeriodBinding(){
        reverseFractionBind(basePeriodSpinner, frequencySpinner);
        reverseFractionBind(frequencySpinner, basePeriodSpinner);
    }

    private void reverseFractionBind(Spinner<Double> baseFractionSpinner, Spinner<Double> reverseFractionSpinner) {
        baseFractionSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal >= 0.01) {
                double expectedFreq = 1.0 / newVal;
                if (expectedFreq < 0.01) expectedFreq = 0.01;
                if (reverseFractionSpinner.getValue() == null || Math.abs(reverseFractionSpinner.getValue() - expectedFreq) > 1e-6) {
                    reverseFractionSpinner.getValueFactory().setValue(expectedFreq);
                }
            } else if (newVal != null && newVal < 0.01) {
                baseFractionSpinner.getValueFactory().setValue(0.01);
            }
        });
    }

    private void setupSignalSettingsAvailability() {
        generateButton.disableProperty().bind(signalSelection.selectedSignal().isNull());
        generalSignalSettingsPane.disableProperty().bind(signalSelection.selectedSignal().isNull());
        specificSignalSettingsVBox.disableProperty().bind(signalSelection.selectedSignal().isNull());
    }

    private void setupParameterUpdateOnSignalChange() {
        signalSelection.selectedSignal().addListener((observable, oldVa, newVal) -> {
            if (newVal != null) {
                if (newVal.getGenerator() != null) {
                    SignalParameters params = newVal.getGenerator().getParameters();
                    signalTypeComboBox.getSelectionModel().select(newVal.getGenerator().getSignalType());

                    amplitudeSpinner.getValueFactory().setValue(params.getAmplitude());
                    if (startTimeSpinner != null) startTimeSpinner.getValueFactory().setValue(params.getStartTime());
                    if (durationSpinner != null) durationSpinner.getValueFactory().setValue(params.getDuration());

                    if (basePeriodSpinner != null) basePeriodSpinner.getValueFactory().setValue(params.getPeriod());
                    if (dutyCycleSpinner != null) dutyCycleSpinner.getValueFactory().setValue(params.getDutyCycle());
                    if (jumpTimeSpinner != null) jumpTimeSpinner.getValueFactory().setValue(params.getJumpTime());
                    if (probabilitySpinner != null) probabilitySpinner.getValueFactory().setValue(params.getProbability());

                    if (firstSampleSpinner != null) firstSampleSpinner.getValueFactory().setValue(params.getFirstSample());
                    if (jumpSampleSpinner != null) jumpSampleSpinner.getValueFactory().setValue(params.getJumpSample());
                    if (sampleLengthSpinner != null) sampleLengthSpinner.getValueFactory().setValue(params.getSampleLength());
                }
                if (samplingRateSpinner != null) samplingRateSpinner.getValueFactory().setValue(newVal.getSamplingFrequency());
            }
        });
    }
}
