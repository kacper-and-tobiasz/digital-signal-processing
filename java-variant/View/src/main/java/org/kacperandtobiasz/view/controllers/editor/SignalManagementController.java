package org.kacperandtobiasz.view.controllers.editor;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.kacperandtobiasz.model.base.signal.*;
import org.kacperandtobiasz.model.storage.SignalFileHandler;
import org.kacperandtobiasz.view.MainContext;

import java.io.File;
import java.io.IOException;

public class SignalManagementController {
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
    public Button save_button;
    @FXML
    public Button load_button;



    public SignalManagementController(MainContext mainContext) {
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
        selectedSignalSampled.set(targetSignal.isSampled());

        // Refresh combobox to force cell update if needed (so name etc stay in sync visibly)
        int selectedIndex = signal_selector.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            boolean sel1Match = (signal_selector1 != null && signal_selector1.getValue() == targetSignal);
            boolean sel2Match = (signal_selector2 != null && signal_selector2.getValue() == targetSignal);

            signals.set(selectedIndex, targetSignal);

            signal_selector.getSelectionModel().select(selectedIndex);
            if (sel1Match && signal_selector1 != null) signal_selector1.getSelectionModel().select(targetSignal);
            if (sel2Match && signal_selector2 != null) signal_selector2.getSelectionModel().select(targetSignal);
        }

        redrawCharts();
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
                boolean sel1Match = (signal_selector1 != null && signal_selector1.getValue() == selected);
                boolean sel2Match = (signal_selector2 != null && signal_selector2.getValue() == selected);

                selected.setName(currentText);
                int selectedIndex = signal_selector.getSelectionModel().getSelectedIndex();
                signals.set(selectedIndex, selected);

                signal_selector.getSelectionModel().select(selectedIndex);
                if (sel1Match && signal_selector1 != null) signal_selector1.getSelectionModel().select(selected);
                if (sel2Match && signal_selector2 != null) signal_selector2.getSelectionModel().select(selected);

                redrawCharts();
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

    private final SignalFileHandler fileHandler = new SignalFileHandler();

    @FXML
    private void handleSaveSignal() {
        Signal selected = signal_selector.getSelectionModel().getSelectedItem();

        if (selected == null || !selected.isSampled()) {
            showError("Błąd zapisu", "Sygnał musi być wygenerowany (spróbkowany) przed zapisaniem.");
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
                showError("Błąd zapisu", "Nie udało się zapisać pliku.\n" + e.getMessage());
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

                boolean selMainMatch = (signal_selector != null && signal_selector.getValue() == existingSignal);
                boolean sel1Match = (signal_selector1 != null && signal_selector1.getValue() == existingSignal);
                boolean sel2Match = (signal_selector2 != null && signal_selector2.getValue() == existingSignal);

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

                signal_selector.getSelectionModel().select(loadedSignal);

                if (sel1Match && signal_selector1 != null) {
                    signal_selector1.getSelectionModel().select(loadedSignal);
                }
                if (sel2Match && signal_selector2 != null) {
                    signal_selector2.getSelectionModel().select(loadedSignal);
                }

                redrawCharts();

            } catch (Exception e) {
                showError("Błąd wczytywania", "Nie udało się zdeserializować pliku. Uszkodzone lub brakujące dane.\n" + e.getMessage());
            }
        }
    }
}
