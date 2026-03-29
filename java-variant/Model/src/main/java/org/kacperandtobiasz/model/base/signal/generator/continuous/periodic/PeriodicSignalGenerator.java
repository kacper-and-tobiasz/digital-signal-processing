package org.kacperandtobiasz.model.base.signal.generator.continuous.periodic;

import org.kacperandtobiasz.model.base.signal.generator.continuous.ContinuousSignalGenerator;

public abstract class PeriodicSignalGenerator extends ContinuousSignalGenerator {

    private double period;

    public PeriodicSignalGenerator(double amplitude, double startTime, double duration, double period) {
        super(amplitude, startTime, duration);
        this.period = period;
    }

    public PeriodicSignalGenerator() {
        super();
        this.period = 1.0;
    }

    public double getPeriod() {
        return period;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    protected double phaseWithinPeriod(double time) {
        double phase = (time - getStartTime()) % period;
        return phase < 0 ? phase + period : phase;
    }
}