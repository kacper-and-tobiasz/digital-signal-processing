package org.kacperandtobiasz.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.kacperandtobiasz.model.base.signal.SignalType;

public record SignalParameterState(
        ObjectProperty<SignalType> signalType,
        DoubleProperty amplitude,
        DoubleProperty signalStart,
        DoubleProperty signalDuration,
        DoubleProperty basePeriod,
        DoubleProperty dutyCycle,
        DoubleProperty samplingRate,
        DoubleProperty jumpTime,
        DoubleProperty probability,
        IntegerProperty firstSample,
        IntegerProperty jumpSample,
        IntegerProperty sampleLength
) {
    public SignalParameterState() {
        this(
                new SimpleObjectProperty<>(SignalType.SIN),
                new SimpleDoubleProperty(1.0),
                new SimpleDoubleProperty(0.0),
                new SimpleDoubleProperty(1.0),
                new SimpleDoubleProperty(1.0),
                new SimpleDoubleProperty(0.5),
                new SimpleDoubleProperty(100.0),
                new SimpleDoubleProperty(0.0),
                new SimpleDoubleProperty(0.5),
                new SimpleIntegerProperty(0),
                new SimpleIntegerProperty(0),
                new SimpleIntegerProperty(100)
        );
    }

    public SignalType getSignalType() {
        return signalType.get();
    }

    public void setSignalType(SignalType value) {
        signalType.set(value);
    }

    public double getAmplitude() {
        return amplitude.get();
    }

    public void setAmplitude(double value) {
        amplitude.set(value);
    }

    public double getSignalStart() {
        return signalStart.get();
    }

    public void setSignalStart(double value) {
        signalStart.set(value);
    }

    public double getSignalDuration() {
        return signalDuration.get();
    }

    public void setSignalDuration(double value) {
        signalDuration.set(value);
    }

    public double getBasePeriod() {
        return basePeriod.get();
    }

    public void setBasePeriod(double value) {
        basePeriod.set(value);
    }

    public double getDutyCycle() {
        return dutyCycle.get();
    }

    public void setDutyCycle(double value) {
        dutyCycle.set(value);
    }

    public double getSamplingRate() {
        return samplingRate.get();
    }

    public void setSamplingRate(double value) {
        samplingRate.set(value);
    }

    public double getJumpTime() {
        return jumpTime.get();
    }

    public void setJumpTime(double value) {
        jumpTime.set(value);
    }

    public double getProbability() {
        return probability.get();
    }

    public void setProbability(double value) {
        probability.set(value);
    }

    public int getFirstSample() {
        return firstSample.get();
    }

    public void setFirstSample(int value) {
        firstSample.set(value);
    }

    public int getJumpSample() {
        return jumpSample.get();
    }

    public void setJumpSample(int value) {
        jumpSample.set(value);
    }

    public int getSampleLength() {
        return sampleLength.get();
    }

    public void setSampleLength(int value) {
        sampleLength.set(value);
    }
}
