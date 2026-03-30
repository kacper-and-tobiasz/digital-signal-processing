package org.kacperandtobiasz.model.base.signal.generator.continuous.periodic.dutycycle;

import org.kacperandtobiasz.model.base.signal.generator.continuous.periodic.PeriodicSignalGenerator;
import org.kacperandtobiasz.model.base.signal.SignalParameters;

public abstract class DutyCycleSignalGenerator extends PeriodicSignalGenerator {

    private double dutyCycle;

    public DutyCycleSignalGenerator(double amplitude, double startTime, double duration, double period, double dutyCycle) {
        super(amplitude, startTime, duration, period);
        this.dutyCycle = dutyCycle;
    }

    public DutyCycleSignalGenerator() {
        super();
        this.dutyCycle = 0.5;
    }

    public double getDutyCycle() {
        return dutyCycle;
    }

    public void setDutyCycle(double dutyCycle) {
        this.dutyCycle = dutyCycle;
    }

    @Override
    public SignalParameters getParameters() {
        return super.getParameters().withDutyCycle(dutyCycle);
    }
}