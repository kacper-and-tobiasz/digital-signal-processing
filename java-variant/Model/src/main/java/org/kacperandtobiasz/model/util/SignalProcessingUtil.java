package org.kacperandtobiasz.model.util;

import org.kacperandtobiasz.model.base.signal.DiscreteSignal;

public final class SignalProcessingUtil {
    private static final double EPSILON = 1e-12;

    private SignalProcessingUtil() {
    }

    public enum WindowType {
        RECTANGULAR,
        HAMMING
    }

    public enum FilterType {
        LOW_PASS,
        HIGH_PASS
    }

    public static double[] designFirFilter(int coefficientCount, double cutoffFrequency, double samplingFrequency, FilterType filterType, WindowType windowType) {
        validateCoefficientCount(coefficientCount);
        validateSamplingFrequency(samplingFrequency);
        validateCutoffFrequency(cutoffFrequency, samplingFrequency);

        double[] lowPass = designLowPassCoefficients(coefficientCount, cutoffFrequency, samplingFrequency, windowType);
        if (filterType == FilterType.LOW_PASS) {
            return lowPass;
        }
        return designHighPassCoefficients(lowPass);
    }

    public static DiscreteSignal applyFirFilter(DiscreteSignal input, double[] coefficients) {
        validateSignal(input);
        validateCoefficients(coefficients);
        DiscreteSignal kernel = new DiscreteSignal(coefficients, input.samplingFrequency(), 0.0);
        return convolve(input, kernel);
    }

    public static DiscreteSignal convolve(DiscreteSignal first, DiscreteSignal second) {
        validateSignal(first);
        validateSignal(second);
        validateSameSamplingFrequency(first, second);

        double[] firstSamples = first.samples();
        double[] secondSamples = second.samples();
        if (firstSamples.length == 0 || secondSamples.length == 0) {
            throw new IllegalArgumentException("Signals must contain at least one sample.");
        }

        double[] result = new double[firstSamples.length + secondSamples.length - 1];
        for (int i = 0; i < firstSamples.length; i++) {
            for (int j = 0; j < secondSamples.length; j++) {
                result[i + j] += firstSamples[i] * secondSamples[j];
            }
        }

        return new DiscreteSignal(result, first.samplingFrequency(), first.startTime() + second.startTime());
    }

    public static DiscreteSignal correlateDirect(DiscreteSignal first, DiscreteSignal second) {
        validateSignal(first);
        validateSignal(second);
        validateSameSamplingFrequency(first, second);

        double[] firstSamples = first.samples();
        double[] secondSamples = second.samples();
        if (firstSamples.length == 0 || secondSamples.length == 0) {
            throw new IllegalArgumentException("Signals must contain at least one sample.");
        }

        int resultLength = firstSamples.length + secondSamples.length - 1;
        double[] result = new double[resultLength];
        int lagOffset = secondSamples.length - 1;

        for (int resultIndex = 0; resultIndex < resultLength; resultIndex++) {
            int lag = resultIndex - lagOffset;
            double sum = 0.0;
            for (int firstIndex = 0; firstIndex < firstSamples.length; firstIndex++) {
                int secondIndex = firstIndex - lag;
                if (secondIndex >= 0 && secondIndex < secondSamples.length) {
                    sum += firstSamples[firstIndex] * secondSamples[secondIndex];
                }
            }
            result[resultIndex] = sum;
        }

        double startTime = first.startTime() - second.startTime() - ((double) (secondSamples.length - 1) / first.samplingFrequency());
        return new DiscreteSignal(result, first.samplingFrequency(), startTime);
    }

    public static DiscreteSignal correlateUsingConvolution(DiscreteSignal first, DiscreteSignal second) {
        validateSignal(first);
        validateSignal(second);
        validateSameSamplingFrequency(first, second);

        double[] secondSamples = second.samples();
        double[] reversed = new double[secondSamples.length];
        for (int i = 0; i < secondSamples.length; i++) {
            reversed[i] = secondSamples[secondSamples.length - 1 - i];
        }

        DiscreteSignal reversedSignal = new DiscreteSignal(
                reversed,
                second.samplingFrequency(),
                -second.startTime() - ((double) (secondSamples.length - 1) / second.samplingFrequency())
        );
        return convolve(first, reversedSignal);
    }

    private static double[] designLowPassCoefficients(int coefficientCount, double cutoffFrequency, double samplingFrequency, WindowType windowType) {
        double[] coefficients = new double[coefficientCount];
        int middle = coefficientCount / 2;
        double normalizedCutoff = cutoffFrequency / samplingFrequency;

        for (int n = 0; n < coefficientCount; n++) {
            int offset = n - middle;
            double ideal = offset == 0
                    ? 2.0 * normalizedCutoff
                    : Math.sin(2.0 * Math.PI * normalizedCutoff * offset) / (Math.PI * offset);
            coefficients[n] = ideal * windowValue(n, coefficientCount, windowType);
        }

        return coefficients;
    }

    private static double[] designHighPassCoefficients(double[] lowPassCoefficients) {
        double[] highPass = new double[lowPassCoefficients.length];
        for (int i = 0; i < lowPassCoefficients.length; i++) {
            highPass[i] = -lowPassCoefficients[i];
        }
        highPass[lowPassCoefficients.length / 2] += 1.0;
        return highPass;
    }

    private static double windowValue(int index, int coefficientCount, WindowType windowType) {
        return switch (windowType) {
            case RECTANGULAR -> 1.0;
            case HAMMING -> 0.53836 - 0.46164 * Math.cos((2.0 * Math.PI * index) / coefficientCount);
        };
    }

    private static void validateSignal(DiscreteSignal signal) {
        if (signal == null) {
            throw new IllegalArgumentException("Signal must not be null.");
        }
    }

    private static void validateCoefficients(double[] coefficients) {
        if (coefficients == null || coefficients.length == 0) {
            throw new IllegalArgumentException("Filter must contain at least one coefficient.");
        }
    }

    private static void validateCoefficientCount(int coefficientCount) {
        if (coefficientCount < 1 || coefficientCount % 2 == 0) {
            throw new IllegalArgumentException("Filter coefficient count must be a positive odd number.");
        }
    }

    private static void validateSamplingFrequency(double samplingFrequency) {
        if (!(samplingFrequency > 0.0)) {
            throw new IllegalArgumentException("Sampling frequency must be greater than zero.");
        }
    }

    private static void validateCutoffFrequency(double cutoffFrequency, double samplingFrequency) {
        if (!(cutoffFrequency > 0.0) || cutoffFrequency >= samplingFrequency / 2.0) {
            throw new IllegalArgumentException("Cutoff frequency must be greater than zero and lower than half the sampling frequency.");
        }
    }

    private static void validateSameSamplingFrequency(DiscreteSignal first, DiscreteSignal second) {
        if (Math.abs(first.samplingFrequency() - second.samplingFrequency()) > EPSILON) {
            throw new IllegalArgumentException("Signals must have the same sampling frequency.");
        }
    }
}
