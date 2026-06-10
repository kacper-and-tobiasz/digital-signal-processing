package org.kacperandtobiasz.model.util;

import org.kacperandtobiasz.model.base.signal.ComplexNumber;
import org.kacperandtobiasz.model.base.signal.ComplexSignal;
import org.kacperandtobiasz.model.base.signal.DiscreteSignal;
import org.kacperandtobiasz.model.base.signal.TransformResult;

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

    public enum FourierMethod {
        DEFINITION,
        FFT_DIF
    }

    public enum DctMethod {
        DEFINITION,
        FAST
    }

    public static TransformResult transformFourier(DiscreteSignal input, FourierMethod method) {
        input = fitToTransformLength(input);
        long start = System.nanoTime();
        ComplexSignal result = method == FourierMethod.FFT_DIF ? fftDif(input) : dft(input);
        return new TransformResult(result, null, System.nanoTime() - start);
    }

    public static TransformResult transformDctII(DiscreteSignal input, DctMethod method) {
        input = fitToTransformLength(input);
        long start = System.nanoTime();
        DiscreteSignal result = method == DctMethod.FAST ? fastDctII(input) : dctII(input);
        return new TransformResult(null, result, System.nanoTime() - start);
    }

    public static DiscreteSignal createS1Signal() {
        double samplingFrequency = 16.0;
        double duration = 6.0;
        int count = (int) (samplingFrequency * duration);
        double[] samples = new double[count];
        for (int i = 0; i < count; i++) {
            double t = i / samplingFrequency;
            samples[i] = 2.0 * Math.sin((2.0 * Math.PI / 2.0 )* t + Math.PI / 2.0)
                    + 5.0 * Math.sin((2.0 * Math.PI / 0.5 )* t + Math.PI / 2.0);
        }
        return new DiscreteSignal(samples, samplingFrequency, 0.0);
    }

    public static ComplexSignal dft(DiscreteSignal input) {
        validatePowerOfTwoSignal(input);
        double[] source = input.samples();
        int n = source.length;
        ComplexNumber[] result = new ComplexNumber[n];
        for (int m = 0; m < n; m++) {
            double real = 0.0;
            double imaginary = 0.0;
            for (int k = 0; k < n; k++) {
                double angle = -2.0 * Math.PI * m * k / n;
                real += source[k] * Math.cos(angle);
                imaginary += source[k] * Math.sin(angle);
            }
            result[m] = new ComplexNumber(real / n, imaginary / n);
        }
        return new ComplexSignal(result, input.samplingFrequency(), 0.0);
    }

    public static ComplexSignal fftDif(DiscreteSignal input) {
        validatePowerOfTwoSignal(input);
        double[] source = input.samples();
        ComplexNumber[] data = new ComplexNumber[source.length];
        for (int i = 0; i < source.length; i++) {
            data[i] = new ComplexNumber(source[i], 0.0);
        }

        fftDifInPlace(data);
        bitReverse(data);

        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].multiply(1.0 / data.length);
        }
        return new ComplexSignal(data, input.samplingFrequency(), 0.0);
    }

    public static DiscreteSignal dctII(DiscreteSignal input) {
        validatePowerOfTwoSignal(input);
        double[] source = input.samples();
        int n = source.length;
        double[] result = new double[n];
        for (int m = 0; m < n; m++) {
            double sum = 0.0;
            for (int k = 0; k < n; k++) {
                sum += source[k] * Math.cos(Math.PI * (2.0 * k + 1.0) * m / (2.0 * n));
            }
            result[m] = dctScale(m, n) * sum;
        }
        return new DiscreteSignal(result, input.samplingFrequency(), 0.0);
    }

    public static DiscreteSignal fastDctII(DiscreteSignal input) {
        validatePowerOfTwoSignal(input);
        double[] source = input.samples();
        int n = source.length;
        double[] reordered = new double[n];
        for (int i = 0; i < n / 2; i++) {
            reordered[i] = source[2 * i];
            reordered[n - 1 - i] = source[2 * i + 1];
        }

        ComplexNumber[] data = new ComplexNumber[n];
        for (int i = 0; i < n; i++) {
            data[i] = new ComplexNumber(reordered[i], 0.0);
        }
        fftDifInPlace(data);
        bitReverse(data);

        double[] result = new double[n];
        for (int m = 0; m < n; m++) {
            double angle = -Math.PI * m / (2.0 * n);
            ComplexNumber factor = new ComplexNumber(Math.cos(angle), Math.sin(angle));
            result[m] = dctScale(m, n) * data[m].multiply(factor).real();
        }
        return new DiscreteSignal(result, input.samplingFrequency(), 0.0);
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

    private static void validatePowerOfTwoSignal(DiscreteSignal signal) {
        validateSignal(signal);
        int count = signal.getSampleCount();
        if (count < 2 || count > 1024 || (count & (count - 1)) != 0) {
            throw new IllegalArgumentException("Sample count must be a power of two between 2 and 1024.");
        }
    }

    private static DiscreteSignal fitToTransformLength(DiscreteSignal signal) {
        validateSignal(signal);
        int count = signal.getSampleCount();
        if (count < 2) {
            throw new IllegalArgumentException("Signal must contain at least two samples.");
        }

        int targetCount = 2;
        while (targetCount < count && targetCount < 1024) {
            targetCount *= 2;
        }

        if (count == targetCount) {
            return signal;
        }

        double[] source = signal.samples();
        double[] fitted = new double[targetCount];
        System.arraycopy(source, 0, fitted, 0, Math.min(source.length, targetCount));
        return new DiscreteSignal(fitted, signal.samplingFrequency(), signal.startTime());
    }

    private static double dctScale(int index, int count) {
        return index == 0 ? Math.sqrt(1.0 / count) : Math.sqrt(2.0 / count);
    }

    private static void fftDifInPlace(ComplexNumber[] data) {
        int n = data.length;
        for (int size = n; size >= 2; size /= 2) {
            int half = size / 2;
            for (int start = 0; start < n; start += size) {
                for (int j = 0; j < half; j++) {
                    ComplexNumber top = data[start + j];
                    ComplexNumber bottom = data[start + j + half];
                    ComplexNumber sum = top.add(bottom);
                    ComplexNumber difference = top.subtract(bottom);
                    double angle = -2.0 * Math.PI * j / size;
                    ComplexNumber twiddle = new ComplexNumber(Math.cos(angle), Math.sin(angle));
                    data[start + j] = sum;
                    data[start + j + half] = difference.multiply(twiddle);
                }
            }
        }
    }

    private static void bitReverse(ComplexNumber[] data) {
        int bits = Integer.numberOfTrailingZeros(data.length);
        for (int i = 0; i < data.length; i++) {
            int reversed = Integer.reverse(i) >>> (Integer.SIZE - bits);
            if (reversed > i) {
                ComplexNumber temp = data[i];
                data[i] = data[reversed];
                data[reversed] = temp;
            }
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
