package org.kacperandtobiasz.model.base;

import org.kacperandtobiasz.model.base.signal.Signal;

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

    /**
     * Replaces underlying repository list.
     * 
     * @param backingList a list to fill with existing signals and to be used inside a repository.
     */
    public void setBackingList(List<Signal> backingList){
        backingList.addAll(signals);
        this.signals = backingList;
    }
}
