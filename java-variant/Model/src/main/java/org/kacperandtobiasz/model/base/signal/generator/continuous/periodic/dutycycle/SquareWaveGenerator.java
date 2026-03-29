package org.kacperandtobiasz.model.base.signal.generator.continuous.periodic.dutycycle;

import org.kacperandtobiasz.model.base.signal.SignalType;

public class SquareWaveGenerator extends DutyCycleSignalGenerator {

    public SquareWaveGenerator(double amplitude, double startTime, double duration, double period, double dutyCycle) {
        super(amplitude, startTime, duration, period, dutyCycle);
    }

    public SquareWaveGenerator() {
        super();
    }

    @Override
    public SignalType getSignalType() {
        return SignalType.RECT;
    }

    @Override
    protected double computeValue(double time) {
        return phaseWithinPeriod(time) < getDutyCycle() * getPeriod() ? getAmplitude() : 0.0;
    }
}