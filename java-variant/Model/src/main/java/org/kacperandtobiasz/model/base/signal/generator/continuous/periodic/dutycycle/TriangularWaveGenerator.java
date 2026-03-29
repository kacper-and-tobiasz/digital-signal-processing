package org.kacperandtobiasz.model.base.signal.generator.continuous.periodic.dutycycle;

import org.kacperandtobiasz.model.base.signal.SignalType;

public class TriangularWaveGenerator extends DutyCycleSignalGenerator {

    public TriangularWaveGenerator(double amplitude, double startTime, double duration, double period, double dutyCycle) {
        super(amplitude, startTime, duration, period, dutyCycle);
    }

    public TriangularWaveGenerator() {
        super();
    }

    @Override
    public SignalType getSignalType() {
        return SignalType.TRIAN;
    }

    @Override
    protected double computeValue(double time) {
        double T = getPeriod();
        double A = getAmplitude();
        double kw = getDutyCycle();
        double phase = phaseWithinPeriod(time);

        if (phase < kw * T) {
            return (A / (kw * T)) * phase;
        } else {
            return (-A / (T * (1.0 - kw))) * phase + A / (1.0 - kw);
        }
    }
}