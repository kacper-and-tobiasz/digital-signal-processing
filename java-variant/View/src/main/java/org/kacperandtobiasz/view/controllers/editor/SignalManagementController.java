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
import org.kacperandtobiasz.view.services.GraphService;
import org.kacperandtobiasz.view.utils.AlertUtil;

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
    @FXML
    public Button previewButton;


    private final SignalRepository signalRepo;
    private final SignalSelectionState signalSelection;
    private final GraphService graphService;
    private ObservableList<Signal> signals;

    public SignalManagementController(MainContext mainContext) {
        this.signalRepo = mainContext.signalRepository();
        this.signalSelection = mainContext.signalSelectionState();
        this.graphService = mainContext.graphService();
    }

    @FXML
    private void initialize() {
        if (signalRepo.getSignals() instanceof ObservableList<Signal> signals) {
            this.signals = signals;
            signalSelectorComboBox.setItems(signals);
        }
        if (signalSelectorComboBox != null) {
            signalSelectorComboBox.valueProperty().bindBidirectional(signalSelection.selectedSignal());
            signalSelectorComboBox.valueProperty().addListener((obs, oldVal, newVal) -> graphService.drawResultSignalGraphs(newVal));
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
        String signalName = signalNameField.getText();

        Signal signal = new Signal(signalName, null, 100.0);

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
            AlertUtil.showError("Błąd zapisu", "Sygnał musi być wygenerowany (spróbkowany) przed zapisaniem.");
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
                AlertUtil.showError("Błąd zapisu", "Nie udało się zapisać pliku.\n" + e.getMessage());
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
                DiscreteSignal ds = fileHandler.loadFromBinaryFile(file);

                String newName = "Wczytano " + file.getName().replace(".sig", "");
                String temp = newName;
                int counter = 1;
                while(!signalRepo.isSignalNameAvailable(temp)){
                    temp = newName + "(" + counter + ")";
                    counter++;
                }
                Signal loadedSignal = new Signal(temp, ds);

                signalRepo.addSignal(loadedSignal);
                signalSelectorComboBox.getSelectionModel().select(loadedSignal);
//                graphService.drawResultSignalGraphs(loadedSignal);
            } catch (Exception e) {
                AlertUtil.showError("Błąd wczytywania", "Nie udało się zdeserializować pliku. Uszkodzone lub brakujące dane.\n" + e.getMessage());
            }
        }
    }

    @FXML
    private void handlePreviewSignal() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Podgląd sygnału (binarnie)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plik sygnału (*.sig)", "*.sig"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                String previewText = fileHandler.generateTextPreview(file, 20);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Podgląd pliku");
                alert.setHeaderText("Podgląd dla pliku: " + file.getName());
                
                TextArea textArea = new TextArea(previewText);
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                
                javafx.scene.layout.GridPane expContent = new javafx.scene.layout.GridPane();
                expContent.setMaxWidth(Double.MAX_VALUE);
                expContent.add(textArea, 0, 0);
                
                alert.getDialogPane().setContent(expContent);
                alert.showAndWait();
            } catch (Exception e) {
                AlertUtil.showError("Błąd podglądu", "Nie udało się wygenerować podglądu pliku.\n" + e.getMessage());
            }
        }
    }
}
