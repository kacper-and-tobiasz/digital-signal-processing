package org.kacperandtobiasz.model.base.signal.reconstruction;

import org.kacperandtobiasz.model.base.signal.DiscreteSignal;
import org.kacperandtobiasz.model.base.signal.QuantizedRoundedSignal;

public class ZeroOrderHoldReconstructor implements Reconstructor {

    @Override
    public DiscreteSignal reconstruct(QuantizedRoundedSignal signal, double targetSamplingFrequency) {
        double startTime = signal.getStartTime();
        double endTime = signal.getEndTime();
        double duration = endTime - startTime;

        int outputSampleCount = (int) Math.floor(duration * targetSamplingFrequency);
        double[] outputSamples = new double[outputSampleCount];

        double sourceSamplingFrequency = signal.getSamplingFrequency();
        int sourceSampleCount = signal.getSampleCount();

        for (int i = 0; i < outputSampleCount; i++) {
            double t = startTime + i / targetSamplingFrequency;
            int sourceIndex = (int) Math.floor((t - startTime) * sourceSamplingFrequency + 1e-9); //1e-9 dodane jako epsilon, aby np. 0.99999 potraktowac jako 1

            if (sourceIndex < 0) {
                sourceIndex = 0;
            } else if (sourceIndex >= sourceSampleCount) {
                sourceIndex = sourceSampleCount - 1;
            }

            outputSamples[i] = signal.getSample(sourceIndex);
        }

        return new DiscreteSignal(outputSamples, targetSamplingFrequency, startTime);
    }
}
