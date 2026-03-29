package org.kacperandtobiasz.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class Signal {
    private UUID id;
    private String name;
    private double duration, startTime, amplitude;

    public Signal(String name, double duration, double startTime, double amplitude) {
        id = UUID.randomUUID();
        this.name = name;
        this.duration = duration;
        this.startTime = startTime;
        this.amplitude = amplitude;
    }

    public UUID getId() {
        return id;
    }

    public Signal(String name) {
        this(name, 5.0, 0.0, 1.0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    @Override
    public String toString() {
        return name;
    }

}
