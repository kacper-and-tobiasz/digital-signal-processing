package org.kacperandtobiasz.signalanalyzer;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainController {

    @FXML
    public ComboBox signal_selector;
    @FXML
    public TextField signal_name;
    @FXML
    public Button clone_button;
    @FXML
    public Button create_button;
    @FXML
    public Button delete_button;
    @FXML
    public ComboBox signal_type;
    @FXML
    public Spinner signal_start;
    @FXML
    public Spinner signal_duration;
    @FXML
    public Spinner amplitude;
    @FXML
    public Spinner sampling_rate;
    @FXML
    public Spinner base_period;
    @FXML
    public Spinner duty_cycle;
    @FXML
    public Spinner signal_frequency;

}
