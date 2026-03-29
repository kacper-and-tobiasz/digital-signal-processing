package org.kacperandtobiasz.model;

import java.util.*;

public class SignalRepository {
    private List<Signal> signals = new ArrayList<>();

    public void addSignal(Signal signal){
        signals.add(signal);
    }

    public void removeSignal(Signal signal){
        signals.remove(signal);
    }

    public Collection<Signal> getSignals(){
        return signals;
    }

    public void setBackingList(List<Signal> backingList){
        backingList.addAll(signals);
        this.signals = backingList;
    }
}
