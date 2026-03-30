package org.kacperandtobiasz.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.kacperandtobiasz.model.base.SignalRepository;
import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.model.base.signal.SignalFactory;
import org.kacperandtobiasz.model.base.signal.SignalParameters;
import org.kacperandtobiasz.model.base.signal.SignalType;
import org.kacperandtobiasz.model.base.signal.DiscreteSignal;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.beans.binding.Bindings;

public class MainController {

    private final MainContext context;
    private final SignalRepository signalRepo;


    @FXML
    public ComboBox<Signal> signal_selector;
    @FXML
    public TextField signal_name;
    @FXML
    public Button rename_button;
    @FXML
    public Button clone_button;
    @FXML
    public Button create_button;
    @FXML
    public Button delete_button;
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
    public ScatterChart<Number, Number> signal_chart;

    @FXML
    public GridPane general_signal_settings;
    @FXML
    public VBox specific_signal_settings;

    private final ObservableList<Signal> signals = FXCollections.observableArrayList();
    private final ObjectProperty<Signal> selectedSignal = new SimpleObjectProperty<>();
    private final StringProperty newSignalName = new SimpleStringProperty("");
    public BarChart signal_bar_chart;

    public MainController(MainContext mainContext) {
        this.context = mainContext;
        this.signalRepo = mainContext.signalRepository();

        this.signalRepo.setBackingList(signals);
    }

    @FXML
    private void initialize() {
        signal_selector.setItems(signals);

        signal_type.getItems().addAll(SignalType.values());
        signal_type.getSelectionModel().select(SignalType.SIN);

        signal_type.valueProperty().addListener((obs, oldVal, newVal) -> updateControlStates(newVal));
        
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

//        If signal instance is not selected, user can't set parameters for it
        general_signal_settings.disableProperty().bind(signal_selector.valueProperty().isNull());
        specific_signal_settings.disableProperty().bind(signal_selector.valueProperty().isNull());
        generate_button.disableProperty().bind(signal_selector.valueProperty().isNull());

//        Can't clone or delete something that isn't there
        clone_button.disableProperty().bind(signal_selector.valueProperty().isNull());
        delete_button.disableProperty().bind(signal_selector.valueProperty().isNull());

//        Before creation signal has to have a name
        create_button.disableProperty().bind(
                Bindings.createBooleanBinding(() -> {
                    String text = signal_name.getText();
                    if (text == null || text.length() < 3) return true;
                    return signals.stream().anyMatch(s -> s.getName().equals(text));
                }, signal_name.textProperty(), signals)
        );
        
        rename_button.disableProperty().bind(
                Bindings.createBooleanBinding(() -> {
                    if (signal_selector.getValue() == null) return true;
                    String text = signal_name.getText();
                    if (text == null || text.length() < 3) return true;
                    return signals.stream()
                            .filter(s -> s != signal_selector.getValue())
                            .anyMatch(s -> s.getName().equals(text));
                }, signal_name.textProperty(), signals, signal_selector.valueProperty())
        );

        Platform.runLater(() -> {
            Scene scene = signal_name.getScene();
            if (scene != null) {
                scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        scene.getRoot().requestFocus();
                    }
                });
            }
        });

        signal_selector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                signal_name.setText(newVal.getName());
                
                if (newVal.getGenerator() != null) {
                    SignalParameters params = newVal.getGenerator().getParameters();
                    signal_type.getSelectionModel().select(newVal.getGenerator().getSignalType());
                    
                    amplitude.getValueFactory().setValue(params.getAmplitude());
                    if (signal_start != null) signal_start.getValueFactory().setValue(params.getStartTime());
                    if (signal_duration != null) signal_duration.getValueFactory().setValue(params.getDuration());
                    
                    if (base_period != null) base_period.getValueFactory().setValue(params.getPeriod());
                    if (duty_cycle != null) duty_cycle.getValueFactory().setValue(params.getDutyCycle());
                    if (jump_time != null) jump_time.getValueFactory().setValue(params.getJumpTime());
                    if (probability != null) probability.getValueFactory().setValue(params.getProbability());
                    
                    if (first_sample != null) first_sample.getValueFactory().setValue(params.getFirstSample());
                    if (jump_sample != null) jump_sample.getValueFactory().setValue(params.getJumpSample());
                    if (sample_length != null) sample_length.getValueFactory().setValue(params.getSampleLength());
                }
                if (sampling_rate != null) sampling_rate.getValueFactory().setValue(newVal.getSamplingFrequency());

                if (newVal.isSampled()) {
                    drawSignal(newVal);
                } else {
                    signal_chart.getData().clear();
                }
            } else {
                signal_chart.getData().clear();
            }
        });

        updateControlStates(signal_type.getValue());
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

        base_period.setDisable(!usesPeriod);
        signal_frequency.setDisable(!usesPeriod);
        duty_cycle.setDisable(!usesDutyCycle);
        if (jump_time != null) jump_time.setDisable(!usesJumpTime);
        if (probability != null) probability.setDisable(!usesProbability);

        if (signal_start != null) signal_start.setDisable(!usesContinuousParams);
        if (signal_duration != null) signal_duration.setDisable(!usesContinuousParams);

        if (first_sample != null) first_sample.setDisable(!usesDiscreteParams);
        if (jump_sample != null) jump_sample.setDisable(type != SignalType.UNIT_IMPULSE);
        if (sample_length != null) sample_length.setDisable(!usesDiscreteParams);
    }

    private void drawSignal(Signal signal) {
        signal_chart.getData().clear();
        if (signal == null || !signal.isSampled()) {
            return;
        }

        DiscreteSignal ds = signal.getDiscreteSignal();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        for (int i = 0; i < ds.getSampleCount(); i++) {
            series.getData().add(new XYChart.Data<>(ds.getTimeAtIndex(i), ds.getSample(i)));
        }

        signal_chart.getData().add(series);
    }

    @FXML
    private void handleCreateSignal() {
        String newName = signal_name.getText();

        SignalType type = signal_type.getValue();
        if (type == null) {
            throw new IllegalArgumentException("Signal type must be selected");
        }

        Signal signal = new Signal(newName, null, 100.0);

        signalRepo.addSignal(signal);
        signal_selector.getSelectionModel().select(signal);
    }

    @FXML
    private void handleGenerateSignal() {
        Signal targetSignal = signal_selector.getSelectionModel().getSelectedItem();
        if (targetSignal == null) return;

        SignalType type = signal_type.getValue();
        if (type == null) {
            throw new IllegalArgumentException("Signal type must be selected");
        }

        String name = targetSignal.getName();
        double amp = amplitude.getValue() != null ? amplitude.getValue() : 1.0;
        double start = (signal_start != null && signal_start.getValue() != null) ? signal_start.getValue() : 0.0;
        double dur = (signal_duration != null && signal_duration.getValue() != null) ? signal_duration.getValue() : 1.0;
        double period = base_period.getValue() != null ? base_period.getValue() : 1.0;
        double duty = duty_cycle.getValue() != null ? duty_cycle.getValue() : 0.5;
        double samplingRate = sampling_rate.getValue() != null ? sampling_rate.getValue() : 100.0;

        double jump = jump_time.getValue() != null ? jump_time.getValue() : 0.0;
        double prob = probability.getValue() != null ? probability.getValue() : 0.5;

        int firstSamp = (first_sample != null && first_sample.getValue() != null) ? first_sample.getValue() : 0;
        int jumpSamp = (jump_sample != null && jump_sample.getValue() != null) ? jump_sample.getValue() : 0;
        int sampLen = (sample_length != null && sample_length.getValue() != null) ? sample_length.getValue() : 100;

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

        drawSignal(targetSignal);
    }

    @FXML
    private void handleDeleteSignal() {
        Signal signal = signal_selector.getSelectionModel().getSelectedItem();
        if (signal == null) {
            throw new IllegalArgumentException("Signal is null, so it cannot be deleted");
        }

        signalRepo.removeSignal(signal);
    }
    
    @FXML
    private void handleRenameSignal() {
        Signal selected = signal_selector.getSelectionModel().getSelectedItem();
        String currentText = signal_name.getText();
        if (selected != null && currentText != null && currentText.length() >= 3) {
            boolean nameExists = signals.stream()
                    .filter(s -> s != selected)
                    .anyMatch(s -> s.getName().equals(currentText));

            if (!nameExists) {
                selected.setName(currentText);
                int selectedIndex = signal_selector.getSelectionModel().getSelectedIndex();
                signals.set(selectedIndex, selected);
                signal_selector.getSelectionModel().select(selectedIndex);
            } else {
                signal_name.setText(selected.getName());
            }
        }
    }

    @FXML
    private void handleCloneSignal() {
        Signal selected = signal_selector.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Signal cloned = selected.deepCopy();
        signalRepo.addSignal(cloned);
        signal_selector.getSelectionModel().select(cloned);
    }

}
