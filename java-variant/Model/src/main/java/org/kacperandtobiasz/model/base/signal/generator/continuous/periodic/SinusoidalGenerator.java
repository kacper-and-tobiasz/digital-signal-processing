package org.kacperandtobiasz.model.base.signal.generator.continuous.periodic;

import org.kacperandtobiasz.model.base.signal.SignalType;

public class SinusoidalGenerator extends PeriodicSignalGenerator {

    public SinusoidalGenerator(double amplitude, double startTime, double duration, double period) {
        super(amplitude, startTime, duration, period);
    }

    public SinusoidalGenerator() {
        super();
    }

    @Override
    public SignalType getSignalType() {
        return SignalType.SIN;
    }

    @Override
    protected double computeValue(double time) {
        return getAmplitude() * Math.sin((2 * Math.PI / getPeriod()) * (time - getStartTime()));
    }
}