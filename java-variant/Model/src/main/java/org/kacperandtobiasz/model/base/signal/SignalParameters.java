package org.kacperandtobiasz.model.base.signal;

public class SignalParameters {
    private double amplitude;
    private double startTime;
    private double duration;

    private double period = 1.0;
    private double dutyCycle = 0.5;
    private double jumpTime = 0.0;
    private double probability = 0.5;

    private int firstSample = 0;
    private int jumpSample = 0;
    private int sampleLength = 100;

    public SignalParameters(double amplitude, double startTime, double duration) {
        this.amplitude = amplitude;
        this.startTime = startTime;
        this.duration = duration;
    }

    public double getAmplitude() { return amplitude; }
    public void setAmplitude(double amplitude) { this.amplitude = amplitude; }

    public double getStartTime() { return startTime; }
    public void setStartTime(double startTime) { this.startTime = startTime; }

    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }

    public double getPeriod() { return period; }
    public SignalParameters withPeriod(double period) { 
        this.period = period; 
        return this; 
    }

    public double getDutyCycle() { return dutyCycle; }
    public SignalParameters withDutyCycle(double dutyCycle) { 
        this.dutyCycle = dutyCycle; 
        return this; 
    }

    public double getJumpTime() { return jumpTime; }
    public SignalParameters withJumpTime(double jumpTime) { 
        this.jumpTime = jumpTime; 
        return this; 
    }

    public double getProbability() { return probability; }
    public SignalParameters withProbability(double probability) { 
        this.probability = probability; 
        return this; 
    }

    public int getFirstSample() { return firstSample; }
    public SignalParameters withFirstSample(int firstSample) {
        this.firstSample = firstSample;
        return this;
    }

    public int getJumpSample() { return jumpSample; }
    public SignalParameters withJumpSample(int jumpSample) {
        this.jumpSample = jumpSample;
        return this;
    }

    public int getSampleLength() { return sampleLength; }
    public SignalParameters withSampleLength(int sampleLength) {
        this.sampleLength = sampleLength;
        return this;
    }
}
