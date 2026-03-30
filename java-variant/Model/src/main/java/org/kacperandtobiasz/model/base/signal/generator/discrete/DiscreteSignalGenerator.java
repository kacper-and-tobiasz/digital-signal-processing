package org.kacperandtobiasz.model.base.signal.generator.discrete;

import org.kacperandtobiasz.model.base.signal.generator.SignalGenerator;
import org.kacperandtobiasz.model.base.signal.SignalParameters;

public abstract class DiscreteSignalGenerator extends SignalGenerator {

    private double samplingFrequency;
    private long firstSampleIndex;
    private long sampleCount;

    public DiscreteSignalGenerator(double amplitude, double startTime, double duration, double samplingFrequency, long firstSampleIndex, long sampleCount) {
        super(amplitude, startTime, duration);
        this.samplingFrequency = samplingFrequency;
        this.firstSampleIndex = firstSampleIndex;
        this.sampleCount = sampleCount;
    }

    public DiscreteSignalGenerator() {
        super();
        this.samplingFrequency = 1.0;
        this.firstSampleIndex = 0;
        this.sampleCount = 100;
    }

    public double getSamplingFrequency() {
        return samplingFrequency;
    }

    public void setSamplingFrequency(double f) {
        this.samplingFrequency = f;
    }

    public long getFirstSampleIndex() {
        return firstSampleIndex;
    }

    public void setFirstSampleIndex(long n1) {
        this.firstSampleIndex = n1;
    }

    public long getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(long l) {
        this.sampleCount = l;
    }

    @Override
    public SignalParameters getParameters() {
        return super.getParameters()
            .withFirstSample((int) firstSampleIndex)
            .withSampleLength((int) sampleCount);
    }

    protected long timeToIndex(double time) {
        return Math.round(time * samplingFrequency);
    }

    protected boolean isIndexInRange(long n) {
        return n >= firstSampleIndex && n < firstSampleIndex + sampleCount;
    }

    protected abstract double computeDiscreteSample(long n);

    @Override
    public double getValue(double time) {
        long n = timeToIndex(time);
        return isIndexInRange(n) ? computeDiscreteSample(n) : 0.0;
    }
}