package org.kacperandtobiasz.model.base.signal.generator.discrete;

import org.kacperandtobiasz.model.base.signal.SignalType;

public class UnitImpulseGenerator extends DiscreteSignalGenerator {

    private long impulseSampleIndex;

    public UnitImpulseGenerator(double amplitude, long impulseSampleIndex, long firstSampleIndex, long sampleCount, double samplingFrequency) {
        super(amplitude, firstSampleIndex / samplingFrequency, sampleCount / samplingFrequency, samplingFrequency, firstSampleIndex, sampleCount);
        this.impulseSampleIndex = impulseSampleIndex;
    }

    public UnitImpulseGenerator() {
        super();
        this.impulseSampleIndex = 0;
    }

    public long getImpulseSampleIndex() {
        return impulseSampleIndex;
    }

    public void setImpulseSampleIndex(long ns) {
        this.impulseSampleIndex = ns;
    }

    @Override
    public SignalType getSignalType() {
        return SignalType.UNIT_IMPULSE;
    }

    @Override
    protected double computeDiscreteSample(long n) {
        return n == impulseSampleIndex ? getAmplitude() : 0.0;
    }
}