package org.kacperandtobiasz.model.base.signal;

public class QuantizedRoundedSignal {
    private final int[] levels;
    private final int bits;

    private final double samplingFrequency;
    private final double startTime;

    private final double min;
    private final double step;

    public QuantizedRoundedSignal(int[] levels, double samplingFrequency, double startTime, int bits, double min,
            double step) {
        this.levels = levels.clone();
        this.samplingFrequency = samplingFrequency;
        this.startTime = startTime;
        this.bits = bits;
        this.min = min;
        this.step = step;
    }

    public int[] getLevels() {
        return levels.clone();
    }

    public double getSample(int index) {
        return min + levels[index] * step;
    }

    public double[] toSamples() {
        double[] samples = new double[levels.length];
        for (int i = 0; i < levels.length; i++) {
            samples[i] = min + levels[i] * step;
        }
        return samples;
    }

    public int getSampleCount() {
        return levels.length;
    }

    public double getSamplingFrequency() {
        return samplingFrequency;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return startTime + levels.length / samplingFrequency;
    }

    public double getTimeAtIndex(int index) {
        return startTime + index / samplingFrequency;
    }

    public int getBits() {
        return bits;
    }

    public int getNumberOfLevels() {
        return 1 << bits;
    }

    public DiscreteSignal toDiscreteSignal() {
        return new DiscreteSignal(toSamples(), samplingFrequency, startTime);
    }
}
