package org.kacperandtobiasz.view;

import org.kacperandtobiasz.model.base.SignalRepository;

public record MainContext(SignalRepository signalRepository) {

    public MainContext() {
        this(new SignalRepository());
    }
}
