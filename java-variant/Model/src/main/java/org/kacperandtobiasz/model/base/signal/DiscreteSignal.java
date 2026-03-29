package org.kacperandtobiasz.model.base.signal;

public record DiscreteSignal(double[] samples, double samplingFrequency, double startTime) {

    public DiscreteSignal(double[] samples, double samplingFrequency, double startTime) {
        this.samples = samples.clone();
        this.samplingFrequency = samplingFrequency;
        this.startTime = startTime;
    }

    public double getSample(int index) {
        return samples[index];
    }

    @Override
    public double[] samples() {
        return samples.clone();
    }

    public int getSampleCount() {
        return samples.length;
    }

    public double getEndTime() {
        return startTime + samples.length / samplingFrequency;
    }

    public double getTimeAtIndex(int index) {
        return startTime + index / samplingFrequency;
    }
}