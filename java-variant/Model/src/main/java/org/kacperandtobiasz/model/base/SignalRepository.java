package org.kacperandtobiasz.model.base;

import org.kacperandtobiasz.model.base.signal.Signal;
import org.kacperandtobiasz.model.util.SignalUtil;

import java.util.*;

public class SignalRepository {
    private List<Signal> signals;

    public SignalRepository(List<Signal> signals) {
        this.signals = signals;
    }

    public void addSignal(Signal signal){
        if (!isSignalNameAvailable(signal.getName()))
            throw new IllegalArgumentException("Signal name '" + signal.getName() + "' is already taken or invalid.");
        signals.add(signal);
    }

    public void removeSignal(Signal signal){
        signals.remove(signal);
    }

    public List<Signal> getSignals(){
        return signals;
    }

    public void setBackingList(List<Signal> backingList){
        if (signals != null) {
            backingList.addAll(signals);
        }
        this.signals = backingList;
    }

    public boolean isSignalNameTaken(String name){
        return signals.stream().anyMatch(s -> s.getName().equals(name));
    }

    public boolean isSignalNameAvailable(String name){
        return SignalUtil.isNameValid(name) && !isSignalNameTaken(name);
    }
}
