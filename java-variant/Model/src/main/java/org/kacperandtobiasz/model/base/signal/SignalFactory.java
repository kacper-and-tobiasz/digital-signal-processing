package org.kacperandtobiasz.model.base.signal;

import org.kacperandtobiasz.model.base.signal.generator.SignalGenerator;
import org.kacperandtobiasz.model.base.signal.generator.continuous.UnitStepGenerator;
import org.kacperandtobiasz.model.base.signal.generator.continuous.noise.GaussianNoiseGenerator;
import org.kacperandtobiasz.model.base.signal.generator.continuous.noise.UniformNoiseGenerator;
import org.kacperandtobiasz.model.base.signal.generator.continuous.periodic.FullRectifiedSinusoidalGenerator;
import org.kacperandtobiasz.model.base.signal.generator.continuous.periodic.HalfRectifiedSinusoidalGenerator;
import org.kacperandtobiasz.model.base.signal.generator.continuous.periodic.SinusoidalGenerator;
import org.kacperandtobiasz.model.base.signal.generator.continuous.periodic.dutycycle.SquareWaveGenerator;
import org.kacperandtobiasz.model.base.signal.generator.continuous.periodic.dutycycle.SymmetricSquareWaveGenerator;
import org.kacperandtobiasz.model.base.signal.generator.continuous.periodic.dutycycle.TriangularWaveGenerator;
import org.kacperandtobiasz.model.base.signal.generator.discrete.ImpulseNoiseGenerator;
import org.kacperandtobiasz.model.base.signal.generator.discrete.UnitImpulseGenerator;

public class SignalFactory {

    public static Signal create(
            SignalType type, 
            String name, 
            double samplingFrequency,
            SignalParameters params
    ) {
        double amplitude = params.getAmplitude();
        double startTime = params.getStartTime();
        double duration = params.getDuration();
        double period = params.getPeriod();
        double dutyCycle = params.getDutyCycle();
        double jumpTime = params.getJumpTime();
        double probability = params.getProbability();
        long firstSample = params.getFirstSample();
        long jumpSample = params.getJumpSample();
        long sampleLength = params.getSampleLength();

        SignalGenerator generator = switch (type) {
            case SIN -> new SinusoidalGenerator(amplitude, startTime, duration, period);
            case GAUSS_NOISE -> new GaussianNoiseGenerator(amplitude, startTime, duration);
            case UNIFORM_NOISE -> new UniformNoiseGenerator(amplitude, startTime, duration);
            case SIN_HALF_RECT -> new HalfRectifiedSinusoidalGenerator(amplitude, startTime, duration, period);
            case SIN_FULL_RECT -> new FullRectifiedSinusoidalGenerator(amplitude, startTime, duration, period);
            case RECT -> new SquareWaveGenerator(amplitude, startTime, duration, period, dutyCycle);
            case RECT_SYMMETRIC -> new SymmetricSquareWaveGenerator(amplitude, startTime, duration, period, dutyCycle);
            case TRIAN -> new TriangularWaveGenerator(amplitude, startTime, duration, period, dutyCycle);
            case UNIT_JUMP -> new UnitStepGenerator(amplitude, startTime, duration, jumpTime);
            case UNIT_IMPULSE -> new UnitImpulseGenerator(amplitude, jumpSample, firstSample, sampleLength, samplingFrequency);
            case IMPULSE_NOISE -> new ImpulseNoiseGenerator(amplitude, startTime, duration, samplingFrequency, probability);
            default -> throw new UnsupportedOperationException("Generator for " + type + " is not yet implemented.");
        };

        return Signal.sampled(name, generator, samplingFrequency);
    }
}
