package org.kacperandtobiasz.model.base.signal;

public record ComplexSignal(ComplexNumber[] samples, double samplingFrequency, double startFrequency) {
    public ComplexSignal(ComplexNumber[] samples, double samplingFrequency, double startFrequency) {
        this.samples = samples.clone();
        this.samplingFrequency = samplingFrequency;
        this.startFrequency = startFrequency;
    }

    public ComplexNumber getSample(int index) {
        return samples[index];
    }

    @Override
    public ComplexNumber[] samples() {
        return samples.clone();
    }

    public int getSampleCount() {
        return samples.length;
    }

    public double getFrequencyAtIndex(int index) {
        return startFrequency + index * samplingFrequency / samples.length;
    }
}
