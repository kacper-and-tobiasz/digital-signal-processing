package org.kacperandtobiasz.model.base.signal.generator.continuous.noise;

import org.kacperandtobiasz.model.base.signal.SignalType;

public class GaussianNoiseGenerator extends NoiseGenerator {

    public GaussianNoiseGenerator(double amplitude, double startTime, double duration) {
        super(amplitude, startTime, duration);
    }

    public GaussianNoiseGenerator() {
        super();
    }

    @Override
    public SignalType getSignalType() {
        return SignalType.GAUSS_NOISE;
    }

    @Override
    protected double computeValue(double time) {
        return getAmplitude() * getRandom().nextGaussian();
    }
}