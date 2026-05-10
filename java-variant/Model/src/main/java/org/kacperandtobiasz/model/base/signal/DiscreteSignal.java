package org.kacperandtobiasz.model.base.signal;

public record DiscreteSignal(double[] samples, double samplingFrequency, double startTime) {

    public DiscreteSignal(double[] samples, double samplingFrequency, double startTime) {
        this.samples = samples.clone();
        this.samplingFrequency = samplingFrequency;
        this.startTime = startTime;
    }

    public double getSample(int index) {
        return samples[index];
    }

    @Override
    public double[] samples() {
        return samples.clone();
    }

    public int getSampleCount() {
        return samples.length;
    }

    public double getEndTime() {
        return startTime + samples.length / samplingFrequency;
    }

    public double getTimeAtIndex(int index) {
        return startTime + index / samplingFrequency;
    }

    public QuantizedRoundedSignal quantizeWithRounding(int bits) {
        if (bits < 1) {
            throw new IllegalArgumentException("Number of quantization bits must be at least 1.");
        }

        int numLevels = 1 << bits; // 2 ^ bits

        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (double s : samples) {
            if (s < min)
                min = s;
            if (s > max)
                max = s;
        }

        double range = max - min;
        double step = range == 0 ? 1.0 : range / (numLevels - 1);

        int[] levels = new int[samples.length];
        for (int i = 0; i < samples.length; i++) {
            levels[i] = (int) Math.round((samples[i] - min) / step);
        }

        return new QuantizedRoundedSignal(levels, samplingFrequency, startTime, bits, min, step);
    }
}