package org.kacperandtobiasz.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import org.kacperandtobiasz.model.base.SignalRepository;
import org.kacperandtobiasz.view.services.GraphService;

// Class whose object is meant to be DI'd into controller to have a shared state,
// without using a singleton (which would lead to global state).
public record MainContext(
        SignalRepository signalRepository,
        GraphService graphService
) {
    
    public MainContext() {
        this(
                new SignalRepository(),
                new GraphService()
        );
    }


}
