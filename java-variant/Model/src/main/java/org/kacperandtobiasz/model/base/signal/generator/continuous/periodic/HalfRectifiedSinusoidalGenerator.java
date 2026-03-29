package org.kacperandtobiasz.model.base.signal.generator.continuous.periodic;

import org.kacperandtobiasz.model.base.signal.SignalType;

public class HalfRectifiedSinusoidalGenerator extends PeriodicSignalGenerator {

    public HalfRectifiedSinusoidalGenerator(double amplitude, double startTime, double duration, double period) {
        super(amplitude, startTime, duration, period);
    }

    public HalfRectifiedSinusoidalGenerator() {
        super();
    }

    @Override
    public SignalType getSignalType() {
        return SignalType.SIN_HALF_RECT;
    }

    @Override
    protected double computeValue(double time) {
        double s = Math.sin((2 * Math.PI / getPeriod()) * (time - getStartTime()));
        return 0.5 * getAmplitude() * (s + Math.abs(s));
    }
}