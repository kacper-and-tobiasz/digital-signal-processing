package org.kacperandtobiasz.view;

import org.kacperandtobiasz.model.base.SignalRepository;

// Class whose object is meant to be DI'd into controller to have a shared state,
// without using a singletone leading to global state.
public record MainContext(SignalRepository signalRepository) {
    
    public MainContext() {
        this(new SignalRepository());
    }
}
