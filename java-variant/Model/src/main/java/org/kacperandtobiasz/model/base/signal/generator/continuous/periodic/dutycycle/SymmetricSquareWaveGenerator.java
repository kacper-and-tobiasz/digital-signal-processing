package org.kacperandtobiasz.model.base.signal.generator.continuous.periodic.dutycycle;

import org.kacperandtobiasz.model.base.signal.SignalType;


public class SymmetricSquareWaveGenerator extends DutyCycleSignalGenerator {

    public SymmetricSquareWaveGenerator(double amplitude, double startTime, double duration, double period, double dutyCycle) {
        super(amplitude, startTime, duration, period, dutyCycle);
    }

    public SymmetricSquareWaveGenerator() {
        super();
    }

    @Override
    public SignalType getSignalType() {
        return SignalType.RECT_SYMMETRIC;
    }

    @Override
    protected double computeValue(double time) {
        return phaseWithinPeriod(time) < getDutyCycle() * getPeriod() ? getAmplitude() : -getAmplitude();
    }
}