package org.kacperandtobiasz.view.controllers.editor;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.kacperandtobiasz.model.base.SignalRepository;
import org.kacperandtobiasz.model.base.signal.*;
import org.kacperandtobiasz.model.storage.SignalFileHandler;
import org.kacperandtobiasz.view.MainContext;
import org.kacperandtobiasz.view.SignalSelectionState;

import java.io.File;
import java.io.IOException;

public class SignalManagementController {
    @FXML
    public ComboBox<Signal> signalSelectorComboBox;
    @FXML
    public TextField signalNameField;

    @FXML
    public Button renameButton;
    @FXML
    public Button cloneButton;
    @FXML
    public Button createButton;
    @FXML
    public Button deleteButton;

    @FXML
    public Button saveButton;
    @FXML
    public Button loadButton;


    private final SignalRepository signalRepo;
    private final SignalParameterState signalParameters;
    private final SignalSelectionState signalSelection;
    private ObservableList<Signal> signals;

    public SignalManagementController(MainContext mainContext) {
        this.signalRepo = mainContext.signalRepository();
        this.signalParameters = mainContext.signalParameters();
        this.signalSelection = mainContext.signalSelection();
    }

    @FXML
    private void initialize() {
        if (signalRepo.getSignals() instanceof ObservableList<Signal> signals) {
            this.signals = signals;
            signalSelectorComboBox.setItems(signals);
        }
        if (signalSelectorComboBox != null) {
            signalSelectorComboBox.valueProperty().bindBidirectional(signalSelection.selectedSignal());
        }
        setupControlsInteractions();
    }

    private void setupControlsInteractions(){
        // TODO: Can signal be saved when not sampled?
        saveButton.disableProperty().bind(
                signalSelectorComboBox.valueProperty().isNull()
        );

//        Can't clone or delete something that isn't there
        cloneButton.disableProperty().bind(signalSelectorComboBox.valueProperty().isNull());
        deleteButton.disableProperty().bind(signalSelectorComboBox.valueProperty().isNull());

//        New signal name has to be available
        createButton.disableProperty().bind(
            Bindings.createBooleanBinding(
                () -> !signalRepo.isSignalNameAvailable(signalNameField.getText()),
                signalNameField.textProperty(), signals
            )
        );
//        New signal name has to be available and an existing signal has to be selected for renaming.
        renameButton.disableProperty().bind(
            Bindings.createBooleanBinding(
                () -> !isAnySignalSelected() || !signalRepo.isSignalNameAvailable(signalNameField.getText()),
                signalNameField.textProperty(), signals, signalSelectorComboBox.valueProperty()
            )
        );
    }

    private boolean isAnySignalSelected(){
        return signalSelectorComboBox.getValue() != null;
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
        if(selected == null)
            return;

        String newSignalName = signalNameField.getText();
        if(!signalRepo.isSignalNameAvailable(newSignalName))
            return;

        selected.setName(newSignalName);

//        Replacing itself with itself to trigger observable updates
        int selectedIndex = signalSelectorComboBox.getSelectionModel().getSelectedIndex();
        signals.set(selectedIndex, selected);
        signalSelectorComboBox.getSelectionModel().select(selectedIndex);
    }

    @FXML
    private void handleCloneSignal() {
        Signal selected = signalSelectorComboBox.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        Signal cloned = selected.deepCopy();
        String originalName = cloned.getName();
        int cloneIndex = 1;
        while(!signalRepo.isSignalNameAvailable(cloned.getName())){
            cloned.setName(originalName + " (" + cloneIndex + ")");
            cloneIndex++;
        }
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
