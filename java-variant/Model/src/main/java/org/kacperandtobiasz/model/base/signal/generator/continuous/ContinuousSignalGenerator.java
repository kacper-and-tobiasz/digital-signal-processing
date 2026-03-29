package org.kacperandtobiasz.model.base.signal.generator.continuous;

import org.kacperandtobiasz.model.base.signal.generator.SignalGenerator;

public abstract class ContinuousSignalGenerator extends SignalGenerator {

    public ContinuousSignalGenerator(double amplitude, double startTime, double duration) {
        super(amplitude, startTime, duration);
    }

    public ContinuousSignalGenerator() {
        super();
    }

    protected boolean isInRange(double time) {
        return time >= getStartTime() && time <= getStartTime() + getDuration();
    }

    protected abstract double computeValue(double time);

    @Override
    public double getValue(double time) {
        return isInRange(time) ? computeValue(time) : 0.0;
    }
}