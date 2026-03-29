package org.kacperandtobiasz.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.kacperandtobiasz.model.base.SignalRepository;
import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.model.base.signal.SignalFactory;
import org.kacperandtobiasz.model.base.signal.SignalParameters;
import org.kacperandtobiasz.model.base.signal.SignalType;

public class MainController {

    private final MainContext context;
    private final SignalRepository signalRepo;


    @FXML
    public ComboBox<Signal> signal_selector;
    @FXML
    public TextField signal_name;
    @FXML
    public Button clone_button;
    @FXML
    public Button create_button;
    @FXML
    public Button delete_button;
    @FXML
    public ComboBox<SignalType> signal_type;
    @FXML
    public Spinner<Float> signal_start;
    @FXML
    public Spinner<Float> signal_duration;
    @FXML
    public Spinner<Float> amplitude;
    @FXML
    public Spinner<Integer> sampling_rate;
    @FXML
    public Spinner<Float> base_period;
    @FXML
    public Spinner<Float> duty_cycle;
    @FXML
    public Spinner<Float> signal_frequency;
    @FXML
    public Spinner<Float> jump_time;
    @FXML
    public Spinner<Float> probability;
    @FXML
    public Spinner<Integer> first_sample;
    @FXML
    public Spinner<Integer> jump_sample;
    @FXML
    public Spinner<Integer> sample_length;

    @FXML
    public GridPane general_signal_settings;
    @FXML
    public TabPane specific_signal_settings;

    private final ObservableList<Signal> signals = FXCollections.observableArrayList();
    private final ObjectProperty<Signal> selectedSignal = new SimpleObjectProperty<>();
    private final StringProperty newSignalName = new SimpleStringProperty("");

    public MainController(MainContext mainContext){
        this.context = mainContext;
        this.signalRepo = mainContext.signalRepository();

        this.signalRepo.setBackingList(signals);
    }

    @FXML
    private void initialize(){
        signal_selector.setItems(signals);

        signal_type.getItems().addAll(SignalType.values());
        signal_type.getSelectionModel().select(SignalType.SIN);
        
        signal_type.valueProperty().addListener((obs, oldVal, newVal) -> updateControlStates(newVal));

//        If signal instance is not selected, user can't set parameters for it
        general_signal_settings.disableProperty().bind(signal_selector.valueProperty().isNull());
        specific_signal_settings.disableProperty().bind(signal_selector.valueProperty().isNull());

//        Can't clone or delete something that isn't there
        clone_button.disableProperty().bind(signal_selector.valueProperty().isNull());
        delete_button.disableProperty().bind(signal_selector.valueProperty().isNull());

//        Before creation signal has to have a name
        create_button.disableProperty().bind(signal_name.textProperty().length().lessThan(3));
        
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

        base_period.setDisable(!usesPeriod);
        signal_frequency.setDisable(!usesPeriod);
        duty_cycle.setDisable(!usesDutyCycle);
        if(jump_time != null) jump_time.setDisable(!usesJumpTime);
        if(probability != null) probability.setDisable(!usesProbability);
        
        if(first_sample != null) first_sample.setDisable(!usesDiscreteParams);
        if(jump_sample != null) jump_sample.setDisable(type != SignalType.UNIT_IMPULSE);
        if(sample_length != null) sample_length.setDisable(!usesDiscreteParams);
    }

    @FXML
    private void handleCreateSignal(){
        if(signal_name.textProperty().length().lessThan(3).get()){
            throw new IllegalArgumentException("Signal name can't be longer than 3 characters");
        }
        
        SignalType type = signal_type.getValue();
        if (type == null) {
            throw new IllegalArgumentException("Signal type must be selected");
        }

        String name = signal_name.getText();
        double amp = amplitude.getValue() != null ? amplitude.getValue() : 1.0;
        double start = signal_start.getValue() != null ? signal_start.getValue() : 0.0;
        double dur = signal_duration.getValue() != null ? signal_duration.getValue() : 1.0;
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

        Signal signal = SignalFactory.create(type, name, samplingRate, params);

        signalRepo.addSignal(signal);
        signal_selector.getSelectionModel().select(signal);
    }

    @FXML
    private void handleDeleteSignal(){
        Signal signal = signal_selector.getSelectionModel().getSelectedItem();
        if (signal == null) {
            throw new IllegalArgumentException("Signal is null, so it cannot be deleted");
        }

        signalRepo.removeSignal(signal);
    }

}
