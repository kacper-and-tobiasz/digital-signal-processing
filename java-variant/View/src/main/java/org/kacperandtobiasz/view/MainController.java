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
import org.kacperandtobiasz.model.Signal;
import org.kacperandtobiasz.model.SignalRepository;
import org.kacperandtobiasz.model.SignalType;

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

//        If signal instance is not selected, user can't set parameters for it
        general_signal_settings.disableProperty().bind(signal_selector.valueProperty().isNull());
        specific_signal_settings.disableProperty().bind(signal_selector.valueProperty().isNull());

//        Can't clone or delete something that isn't there
        clone_button.disableProperty().bind(signal_selector.valueProperty().isNull());
        delete_button.disableProperty().bind(signal_selector.valueProperty().isNull());

//        Before creation signal has to have a name
        create_button.disableProperty().bind(signal_name.textProperty().length().lessThan(3));
    }

    @FXML
    private void handleCreateSignal(){
        if(signal_name.textProperty().length().lessThan(3).get()){
            throw new IllegalArgumentException("Signal name can't be longer than 3 characters");
        }

        Signal signal = new Signal(signal_name.getText());
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
