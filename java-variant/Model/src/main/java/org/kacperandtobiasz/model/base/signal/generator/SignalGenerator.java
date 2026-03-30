package org.kacperandtobiasz.model.base.signal.generator;

import org.kacperandtobiasz.model.base.signal.SignalType;
import org.kacperandtobiasz.model.base.signal.SignalParameters;

public abstract class SignalGenerator implements Cloneable {

    private double amplitude;
    private double startTime;
    private double duration;

    public SignalGenerator(double amplitude, double startTime, double duration) {
        this.amplitude = amplitude;
        this.startTime = startTime;
        this.duration = duration;
    }

    public SignalGenerator() {
        this(1.0, 0.0, 5.0);
    }

    public abstract SignalType getSignalType();

    public SignalParameters getParameters() {
        return new SignalParameters(getAmplitude(), getStartTime(), getDuration());
    }

    public abstract double getValue(double time);

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Override
    public SignalGenerator clone() {
        try {
            return (SignalGenerator) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}