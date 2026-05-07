package org.kacperandtobiasz.view;

import javafx.collections.FXCollections;
import org.kacperandtobiasz.model.base.SignalRepository;
import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.view.services.GraphService;

// Class whose object is meant to be DI'd into controller to have a shared state,
// without using a singleton (which would lead to global state).
public record MainContext(
        SignalRepository signalRepository,
        GraphService graphService,
        SignalSelectionState signalSelectionState
) {

    public MainContext() {
        this(
                new SignalRepository(FXCollections.observableArrayList()),
                new GraphService(),
                new SignalSelectionState()
        );
    }


}
