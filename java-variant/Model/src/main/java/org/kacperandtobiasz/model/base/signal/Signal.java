package org.kacperandtobiasz.model.base.signal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.kacperandtobiasz.model.base.signal.generator.SignalGenerator;

import java.util.UUID;
import java.util.function.DoubleBinaryOperator;

public class Signal {
    private static final double DIVISION_EPSILON = 1e-12;

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

    public Signal(String name, DiscreteSignal discreteSignal) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.discreteSignal = discreteSignal;
        this.samplingFrequency = discreteSignal.samplingFrequency();
        this.generator = null;
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

    public Signal computeOperation(String outputSignalName, Signal other, DoubleBinaryOperator operator){
        if(other == null){
            throw new IllegalArgumentException("Cannot compute operation with null signal");
        }
        DiscreteSignal sig1 = this.discreteSignal;
        DiscreteSignal sig2 = other.discreteSignal;

        if (sig1 == null || sig2 == null) {
            throw new IllegalStateException("Both signals must be sampled before addition.");
        }
        if (sig1.samplingFrequency() != sig2.samplingFrequency()) {
            throw new IllegalArgumentException("Signals must have the same sampling frequency");
        }

        double samplingFrequency = sig1.samplingFrequency();
        double resultStart = Math.min(sig1.startTime(), sig2.startTime());
        double resultEnd = Math.max(sig1.getEndTime(), sig2.getEndTime());

        int n = (int) Math.round((resultEnd - resultStart) * samplingFrequency);
        if (n <= 0) {
            throw new IllegalStateException("Result signal has non-positive sample count");
        }

        double[] sig1Samples = sig1.samples();
        double[] sig2Samples = sig2.samples();

        int sig1Offset = (int) Math.round((sig1.startTime() - resultStart) * samplingFrequency);
        int sig2Offset = (int) Math.round((sig2.startTime() - resultStart) * samplingFrequency);

        double[] samples = new double[n];
        for (int i = 0; i < n; i++) {
            int sig1Index = i - sig1Offset;
            int sig2Index = i - sig2Offset;

            double a = (sig1Index >= 0 && sig1Index < sig1Samples.length) ? sig1Samples[sig1Index] : 0.0;
            double b = (sig2Index >= 0 && sig2Index < sig2Samples.length) ? sig2Samples[sig2Index] : 0.0;

            samples[i] = operator.applyAsDouble(a, b);
        }

        DiscreteSignal sum = new DiscreteSignal(samples, samplingFrequency, resultStart);
        String name = outputSignalName == null ? this.name + " x " + other.name : outputSignalName;
        return new Signal(name, sum);
    }

    public Signal add(String outputSignalName, Signal other){
        DoubleBinaryOperator addition = (a, b) -> a + b;
        return computeOperation(outputSignalName, other, addition);
    }


    public Signal subtract(String outputSignalName, Signal other){
        DoubleBinaryOperator subtraction = (a, b) -> a - b;
        return computeOperation(outputSignalName, other, subtraction);
    }

    public Signal multiply(String outputSignalName, Signal other){
        DoubleBinaryOperator multiplication = (a, b) -> a * b;
        return computeOperation(outputSignalName, other, multiplication);
    }

    public Signal divide(String outputSignalName, Signal other){
        DoubleBinaryOperator division = (a, b) -> Math.abs(b) < DIVISION_EPSILON ? 0.0 : a / b;
        return computeOperation(outputSignalName, other, division);
    }

    public int countDivisionSkippedSamples(Signal other) {
        if(other == null){
            throw new IllegalArgumentException("Cannot compute operation with null signal");
        }
        DiscreteSignal sig1 = this.discreteSignal;
        DiscreteSignal sig2 = other.discreteSignal;

        if (sig1 == null || sig2 == null) {
            throw new IllegalStateException("Both signals must be sampled before addition.");
        }
        if (sig1.samplingFrequency() != sig2.samplingFrequency()) {
            throw new IllegalArgumentException("Signals must have the same sampling frequency");
        }

        double samplingFrequency = sig1.samplingFrequency();
        double resultStart = Math.min(sig1.startTime(), sig2.startTime());
        double resultEnd = Math.max(sig1.getEndTime(), sig2.getEndTime());

        int n = (int) Math.round((resultEnd - resultStart) * samplingFrequency);
        if (n <= 0) return 0;

        double[] sig2Samples = sig2.samples();
        int sig2Offset = (int) Math.round((sig2.startTime() - resultStart) * samplingFrequency);

        int skipped = 0;
        for (int i = 0; i < n; i++) {
            int sig2Index = i - sig2Offset;
            double denominator = (sig2Index >= 0 && sig2Index < sig2Samples.length) ? sig2Samples[sig2Index] : 0.0;
            if (Math.abs(denominator) < DIVISION_EPSILON) {
                skipped++;
            }
        }

        return skipped;
    }


    public double mean() {
        double sum = 0;
        for (double sample : discreteSignal.samples()) {
            sum += sample;
        }
        return sum / discreteSignal.samples().length;
    }

    public double absoluteMean() {
        double sum = 0;
        for (double sample : discreteSignal.samples()) {
            sum += Math.abs(sample);
        }
        return sum / discreteSignal.samples().length;
    }

    public double averagePower() {
        double sum = 0;
        for (double sample : discreteSignal.samples()) {
            sum += sample * sample;
        }
        return sum / discreteSignal.samples().length;
    }

    public double rms() {
        return Math.sqrt(averagePower());
    }

    public double variance() {
        double[] x = getDiscreteSignal().samples();
        double mean = mean();
        double sum = 0;
        for (double sample : discreteSignal.samples()) {
            sum += (sample - mean) * (sample - mean);
        }
        return sum / discreteSignal.samples().length;
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