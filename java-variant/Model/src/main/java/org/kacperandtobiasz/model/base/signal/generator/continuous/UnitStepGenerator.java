package org.kacperandtobiasz.model.base.signal.generator.continuous;

import org.kacperandtobiasz.model.base.signal.SignalParameters;
import org.kacperandtobiasz.model.base.signal.SignalType;

public class UnitStepGenerator extends ContinuousSignalGenerator {

    private double stepTime;

    public UnitStepGenerator(double amplitude, double startTime, double duration, double stepTime) {
        super(amplitude, startTime, duration);
        this.stepTime = stepTime;
    }

    public UnitStepGenerator() {
        super();
        this.stepTime = 0.0;
    }

    public double getStepTime() {
        return stepTime;
    }

    public void setStepTime(double stepTime) {
        this.stepTime = stepTime;
    }

    @Override
    public SignalType getSignalType() {
        return SignalType.UNIT_JUMP;
    }

    @Override
    public SignalParameters getParameters() {
        return super.getParameters().withJumpTime(stepTime);
    }

    @Override
    protected double computeValue(double time) {
        if (time > stepTime) return getAmplitude();
        else if (time == stepTime) return 0.5 * getAmplitude();
        else return 0.0;
    }
}