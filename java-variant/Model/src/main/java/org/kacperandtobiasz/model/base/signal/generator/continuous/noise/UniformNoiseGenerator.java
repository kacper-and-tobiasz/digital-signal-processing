package org.kacperandtobiasz.model.base.signal.generator.continuous.noise;

import org.kacperandtobiasz.model.base.signal.SignalType;

public class UniformNoiseGenerator extends NoiseGenerator {

    public UniformNoiseGenerator(double amplitude, double startTime, double duration) {
        super(amplitude, startTime, duration);
    }

    public UniformNoiseGenerator() {
        super();
    }

    @Override
    public SignalType getSignalType() {
        return SignalType.UNIFORM_NOISE;
    }

    @Override
    protected double computeValue(double time) {
        return getAmplitude() * (2.0 * getRandom().nextDouble() - 1.0);
    }
}