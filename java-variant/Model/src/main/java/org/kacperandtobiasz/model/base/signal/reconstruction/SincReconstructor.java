package org.kacperandtobiasz.model.base.signal.reconstruction;

import org.kacperandtobiasz.model.base.signal.DiscreteSignal;
import org.kacperandtobiasz.model.base.signal.QuantizedRoundedSignal;

public class SincReconstructor implements Reconstructor {

    @Override
    public DiscreteSignal reconstruct(DiscreteSignal signal, double targetSamplingFrequency) {
        double startTime = signal.startTime();
        double endTime = signal.getEndTime();
        double duration = endTime - startTime;

        int outputSampleCount = (int) Math.floor(duration * targetSamplingFrequency);
        double[] outputSamples = new double[outputSampleCount];

        double sourceSamplingFrequency = signal.samplingFrequency();
        int sourceSampleCount = signal.getSampleCount();
        double T = 1.0 / sourceSamplingFrequency;

        for (int i = 0; i < outputSampleCount; i++) {
            double t = startTime + i / targetSamplingFrequency;
            double sum = 0.0;

            for (int n = 0; n < sourceSampleCount; n++) {
                double tn = startTime + n * T;
                double arg = (t - tn) / T;
                sum += signal.getSample(n) * sinc(arg);
            }

            outputSamples[i] = sum;
        }

        return new DiscreteSignal(outputSamples, targetSamplingFrequency, startTime);
    }

    private double sinc(double x) {
        if (Math.abs(x) < 1e-10) {
            return 1.0;
        }
        return Math.sin(Math.PI * x) / (Math.PI * x);
    }
}
