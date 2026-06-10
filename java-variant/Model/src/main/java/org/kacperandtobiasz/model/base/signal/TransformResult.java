package org.kacperandtobiasz.model.base.signal;

public record TransformResult(ComplexSignal complexSignal, DiscreteSignal realSignal, long elapsedNanos) {
}
