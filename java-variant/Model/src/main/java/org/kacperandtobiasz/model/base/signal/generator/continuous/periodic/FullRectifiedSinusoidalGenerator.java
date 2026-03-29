package org.kacperandtobiasz.model.base.signal.generator.continuous.periodic;

import org.kacperandtobiasz.model.base.signal.SignalType;

public class FullRectifiedSinusoidalGenerator extends PeriodicSignalGenerator {

    public FullRectifiedSinusoidalGenerator(double amplitude, double startTime, double duration, double period) {
        super(amplitude, startTime, duration, period);
    }

    public FullRectifiedSinusoidalGenerator() {
        super();
    }

    @Override
    public SignalType getSignalType() {
        return SignalType.SIN_FULL_RECT;
    }

    @Override
    protected double computeValue(double time) {
        return getAmplitude() * Math.abs(Math.sin((2 * Math.PI / getPeriod()) * (time - getStartTime())));
    }
}