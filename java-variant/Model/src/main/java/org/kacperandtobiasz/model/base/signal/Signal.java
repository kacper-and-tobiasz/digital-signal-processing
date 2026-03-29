package org.kacperandtobiasz.model.base.signal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.kacperandtobiasz.model.base.signal.generator.SignalGenerator;

import java.util.UUID;

public class Signal {
    private final UUID id;

    private String name;
    private SignalGenerator generator;
    private DiscreteSignal discreteSignal;

    private double samplingFrequency;

    public Signal(String name, SignalGenerator generator, double samplingFrequency) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.generator = generator;
        this.samplingFrequency = samplingFrequency;
    }

    public static Signal sampled(String name, SignalGenerator generator, double samplingFrequency) {
        Signal s = new Signal(name, generator, samplingFrequency);
        s.sample();
        return s;
    }


    public void sample() {
        double t1 = generator.getStartTime();
        double dur = generator.getDuration();
        int n = (int) Math.floor(dur * samplingFrequency);

        double[] samples = new double[n];
        for (int i = 0; i < n; i++) {
            double t = t1 + i / samplingFrequency;
            samples[i] = generator.getValue(t);
        }

        this.discreteSignal = new DiscreteSignal(samples, samplingFrequency, t1);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SignalGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(SignalGenerator generator) {
        this.generator = generator;
        this.discreteSignal = null;
    }

    public DiscreteSignal getDiscreteSignal() {
        if (discreteSignal == null) {
            throw new IllegalStateException("Signal has not been sampled yet. Call sample() first.");
        }
        return discreteSignal;
    }

    public boolean isSampled() {
        return discreteSignal != null;
    }

    public double getSamplingFrequency() {
        return samplingFrequency;
    }

    public void setSamplingFrequency(double samplingFrequency) {
        this.samplingFrequency = samplingFrequency;
        this.discreteSignal = null;
    }

    public SignalType getSignalType() {
        return generator.getSignalType();
    }

    public Signal deepCopy() {
        Signal cloned = new Signal("Copy " + this.name, this.generator != null ? this.generator.clone() : null, this.samplingFrequency);
        if (this.isSampled()) {
            cloned.discreteSignal = new DiscreteSignal(
                    this.discreteSignal.samples(), 
                    this.discreteSignal.samplingFrequency(), 
                    this.discreteSignal.startTime()
            );
        }
        return cloned;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Signal other = (Signal) obj;
        return new EqualsBuilder().append(id, other.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }
}