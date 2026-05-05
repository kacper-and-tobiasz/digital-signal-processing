package org.kacperandtobiasz.view.controllers.editor;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.kacperandtobiasz.model.base.SignalRepository;
import org.kacperandtobiasz.model.base.signal.*;
import org.kacperandtobiasz.model.storage.SignalFileHandler;
import org.kacperandtobiasz.view.MainContext;
import org.kacperandtobiasz.view.SignalParameterState;

import java.io.File;
import java.io.IOException;

public class SignalManagementController {
    @FXML
    public ComboBox<Signal> signalSelectorComboBox;
    @FXML
    public TextField signalNameField;
    @FXML
    public Button rename_button;
    @FXML
    public Button clone_button;
    @FXML
    public Button create_button;
    @FXML
    public Button delete_button;

    @FXML
    public Button save_button;
    @FXML
    public Button load_button;


    private final SignalRepository signalRepo;
    private final SignalParameterState signalParameters;
    private ObservableList<Signal> signals;

    public SignalManagementController(MainContext mainContext) {
        this.signalRepo = mainContext.signalRepository();
        this.signalParameters = mainContext.signalParameters();
    }

    @FXML
    private void initialize() {
        if (signalRepo.getSignals() instanceof ObservableList<Signal> signals) {
            this.signals = signals;
            signalSelectorComboBox.setItems(signals);
        }
    }

    @FXML
    private void handleCreateSignal() {
        String newName = signalNameField.getText();

        SignalType type = signalParameters.getSignalType();
        if (type == null) {
            throw new IllegalArgumentException("Signal type must be selected");
        }

        Signal signal = new Signal(newName, null, 100.0);

        signalRepo.addSignal(signal);
        signalSelectorComboBox.getSelectionModel().select(signal);
    }

    @FXML
    private void handleGenerateSignal() {
        Signal targetSignal = signalSelectorComboBox.getSelectionModel().getSelectedItem();
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
//        selectedSignalSampled.set(targetSignal.isSampled());
//
//        // Refresh combobox to force cell update if needed (so name etc stay in sync visibly)
//        int selectedIndex = signalSelectorComboBox.getSelectionModel().getSelectedIndex();
//        if (selectedIndex >= 0) {
//            boolean sel1Match = (signal_selector1 != null && signal_selector1.getValue() == targetSignal);
//            boolean sel2Match = (signal_selector2 != null && signal_selector2.getValue() == targetSignal);
//
//            signals.set(selectedIndex, targetSignal);
//
//            signalSelectorComboBox.getSelectionModel().select(selectedIndex);
//            if (sel1Match && signal_selector1 != null) signal_selector1.getSelectionModel().select(targetSignal);
//            if (sel2Match && signal_selector2 != null) signal_selector2.getSelectionModel().select(targetSignal);
//        }
//
//        redrawCharts();
    }

    @FXML
    private void handleDeleteSignal() {
        Signal signal = signalSelectorComboBox.getSelectionModel().getSelectedItem();
        if (signal == null) {
            throw new IllegalArgumentException("Signal is null, so it cannot be deleted");
        }

        signalRepo.removeSignal(signal);
    }

    @FXML
    private void handleRenameSignal() {
        Signal selected = signalSelectorComboBox.getSelectionModel().getSelectedItem();
        String currentText = signalNameField.getText();
        if (selected != null && currentText != null && currentText.length() >= 3) {
            boolean nameExists = signals.stream()
                    .filter(s -> s != selected)
                    .anyMatch(s -> s.getName().equals(currentText));

//            if (!nameExists) {
//                boolean sel1Match = (signal_selector1 != null && signal_selector1.getValue() == selected);
//                boolean sel2Match = (signal_selector2 != null && signal_selector2.getValue() == selected);
//
//                selected.setName(currentText);
//                int selectedIndex = signalSelectorComboBox.getSelectionModel().getSelectedIndex();
//                signals.set(selectedIndex, selected);
//
//                signalSelectorComboBox.getSelectionModel().select(selectedIndex);
//                if (sel1Match && signal_selector1 != null) signal_selector1.getSelectionModel().select(selected);
//                if (sel2Match && signal_selector2 != null) signal_selector2.getSelectionModel().select(selected);
//
//                redrawCharts();
//            } else {
//                signalNameField.setText(selected.getName());
//            }
        }
    }

    @FXML
    private void handleCloneSignal() {
        Signal selected = signalSelectorComboBox.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Signal cloned = selected.deepCopy();
        signalRepo.addSignal(cloned);
        signalSelectorComboBox.getSelectionModel().select(cloned);
    }

    private final SignalFileHandler fileHandler = new SignalFileHandler();

    @FXML
    private void handleSaveSignal() {
        Signal selected = signalSelectorComboBox.getSelectionModel().getSelectedItem();

        if (selected == null || !selected.isSampled()) {
//            showError("Błąd zapisu", "Sygnał musi być wygenerowany (spróbkowany) przed zapisaniem.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz sygnał (binarnie)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plik sygnału (*.sig)", "*.sig"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                fileHandler.saveToBinaryFile(selected.getDiscreteSignal(), file);
            } catch (IOException e) {
//                showError("Błąd zapisu", "Nie udało się zapisać pliku.\n" + e.getMessage());
            }
        }
    }

    @FXML
    private void handleLoadSignal() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wczytaj sygnał (binarnie)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plik sygnału (*.sig)", "*.sig"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                // Read binary file and construct discrete signal
                DiscreteSignal ds = fileHandler.loadFromBinaryFile(file);

                // Wrap in local Signal entity and generate unique generic name
                String newName = "Wczytano " + file.getName().replace(".sig", "");
                Signal loadedSignal = new Signal(newName, ds);

                Signal existingSignal = signals.stream()
                        .filter(s -> s.getName().equals(newName))
                        .findFirst()
                        .orElse(null);

                boolean selMainMatch = (signalSelectorComboBox != null && signalSelectorComboBox.getValue() == existingSignal);
//                boolean sel1Match = (signal_selector1 != null && signal_selector1.getValue() == existingSignal);
//                boolean sel2Match = (signal_selector2 != null && signal_selector2.getValue() == existingSignal);

                if (existingSignal != null) {
                    int existingSignalIndex = signals.indexOf(existingSignal);
                    if (existingSignalIndex >= 0) {
                        signals.set(existingSignalIndex, loadedSignal);
                    } else {
                        signalRepo.addSignal(loadedSignal);
                    }
                } else {
                    signalRepo.addSignal(loadedSignal);
                }

                signalSelectorComboBox.getSelectionModel().select(loadedSignal);

//                if (sel1Match && signal_selector1 != null) {
//                    signal_selector1.getSelectionModel().select(loadedSignal);
//                }
//                if (sel2Match && signal_selector2 != null) {
//                    signal_selector2.getSelectionModel().select(loadedSignal);
//                }
//
//                redrawCharts();

            } catch (Exception e) {
//                showError("Błąd wczytywania", "Nie udało się zdeserializować pliku. Uszkodzone lub brakujące dane.\n" + e.getMessage());
            }
        }
    }
}
