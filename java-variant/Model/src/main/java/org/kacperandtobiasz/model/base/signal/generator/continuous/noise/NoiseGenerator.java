package org.kacperandtobiasz.model.base.signal.generator.continuous.noise;

import org.kacperandtobiasz.model.base.signal.generator.continuous.ContinuousSignalGenerator;

import java.util.Random;

public abstract class NoiseGenerator extends ContinuousSignalGenerator {

    private final Random random;

    public NoiseGenerator(double amplitude, double startTime, double duration) {
        super(amplitude, startTime, duration);
        this.random = new Random();
    }

    public NoiseGenerator() {
        super();
        this.random = new Random();
    }

    public NoiseGenerator(double amplitude, double startTime, double duration, long seed) {
        super(amplitude, startTime, duration);
        this.random = new Random(seed);
    }

    protected Random getRandom() {
        return random;
    }
}