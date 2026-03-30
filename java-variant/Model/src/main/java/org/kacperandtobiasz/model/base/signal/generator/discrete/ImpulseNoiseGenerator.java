package org.kacperandtobiasz.model.base.signal.generator.discrete;

import org.kacperandtobiasz.model.base.signal.SignalType;
import org.kacperandtobiasz.model.base.signal.SignalParameters;

import java.util.Random;

public class ImpulseNoiseGenerator extends DiscreteSignalGenerator {

    private double probability;
    private final Random random;

    public ImpulseNoiseGenerator(double amplitude, long firstSampleIndex, long sampleCount, double samplingFrequency, double probability) {
        super(amplitude, firstSampleIndex / samplingFrequency, sampleCount / samplingFrequency, samplingFrequency, firstSampleIndex, sampleCount);
        this.probability = probability;
        this.random = new Random();
    }

    public ImpulseNoiseGenerator() {
        super();
        this.probability = 0.5;
        this.random = new Random();
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double p) {
        this.probability = p;
    }

    @Override
    public SignalParameters getParameters() {
        return super.getParameters().withProbability(probability);
    }

    @Override
    public SignalType getSignalType() {
        return SignalType.IMPULSE_NOISE;
    }

    @Override
    protected double computeDiscreteSample(long n) {
        return random.nextDouble() < probability ? getAmplitude() : 0.0;
    }
}