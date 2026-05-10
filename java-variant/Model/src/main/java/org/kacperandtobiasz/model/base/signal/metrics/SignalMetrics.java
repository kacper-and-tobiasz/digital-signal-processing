package org.kacperandtobiasz.model.base.signal.metrics;

import org.kacperandtobiasz.model.base.signal.DiscreteSignal;

public class SignalMetrics {

    public static double calculateMSE(DiscreteSignal original, DiscreteSignal reconstructed) {
        validateSignals(original, reconstructed);
        int n = original.getSampleCount();
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            double diff = original.getSample(i) - reconstructed.getSample(i);
            sum += diff * diff;
        }
        return sum / n;
    }

    public static double calculateSNR(DiscreteSignal original, DiscreteSignal reconstructed) {
        validateSignals(original, reconstructed);
        int n = original.getSampleCount();
        double signalPower = 0.0;
        double noisePower = 0.0;
        for (int i = 0; i < n; i++) {
            signalPower += original.getSample(i) * original.getSample(i);
            double diff = original.getSample(i) - reconstructed.getSample(i);
            noisePower += diff * diff;
        }
        if (noisePower == 0.0)
            return Double.POSITIVE_INFINITY;
        return 10.0 * Math.log10(signalPower / noisePower);
    }

    public static double calculatePSNR(DiscreteSignal original, DiscreteSignal reconstructed) {
        validateSignals(original, reconstructed);
        int n = original.getSampleCount();
        double maxVal = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < n; i++) {
            maxVal = Math.max(maxVal, Math.abs(original.getSample(i)));
        }
        double mse = calculateMSE(original, reconstructed);
        if (mse == 0.0)
            return Double.POSITIVE_INFINITY;
        return 10.0 * Math.log10(maxVal / mse);
    }

    public static double calculateMD(DiscreteSignal original, DiscreteSignal reconstructed) {
        validateSignals(original, reconstructed);
        int n = original.getSampleCount();
        double maxDiff = 0.0;
        for (int i = 0; i < n; i++) {
            maxDiff = Math.max(maxDiff, Math.abs(original.getSample(i) - reconstructed.getSample(i)));
        }
        return maxDiff;
    }

    public static double calculateENOB(DiscreteSignal original, DiscreteSignal reconstructed) {
        double snr = calculateSNR(original, reconstructed);
        return (snr - 1.76) / 6.02;
    }

    private static void validateSignals(DiscreteSignal original, DiscreteSignal reconstructed) {
        if (original == null || reconstructed == null) {
            throw new IllegalArgumentException("Oba sygnały muszą być podane.");
        }
        if (original.getSampleCount() == 0 || reconstructed.getSampleCount() == 0) {
            throw new IllegalArgumentException("Sygnały nie mogą być puste.");
        }
        if (Math.abs(original.samplingFrequency() - reconstructed.samplingFrequency()) > 1e-6) {
            throw new IllegalArgumentException("Sygnały muszą mieć taką samą częstotliwość próbkowania do porównania.");
        }
        if (Math.abs(original.startTime() - reconstructed.startTime()) > 1e-6) {
            throw new IllegalArgumentException("Sygnały muszą mieć ten sam czas początkowy do porównania.");
        }
    }
}
