package org.kacperandtobiasz.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.kacperandtobiasz.model.base.signal.Signal;

public record SignalSelectionState(ObjectProperty<Signal> selectedSignal) {
    public SignalSelectionState() {
        this(new SimpleObjectProperty<>(null));
    }

    public Signal getSelectedSignal() {
        return selectedSignal.get();
    }

    public void setSelectedSignal(Signal signal) {
        selectedSignal.set(signal);
    }
}
