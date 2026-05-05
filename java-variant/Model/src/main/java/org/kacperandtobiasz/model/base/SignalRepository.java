package org.kacperandtobiasz.model.base;

import org.kacperandtobiasz.model.base.signal.Signal;

import java.util.*;

public class SignalRepository {
    private List<Signal> signals;

    public SignalRepository(List<Signal> signals) {
        this.signals = signals;
    }

    public void addSignal(Signal signal){
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
}
