package org.kacperandtobiasz.model.base.signal.reconstruction;

import org.kacperandtobiasz.model.base.signal.DiscreteSignal;
import org.kacperandtobiasz.model.base.signal.QuantizedRoundedSignal;

public interface Reconstructor {
    DiscreteSignal reconstruct(QuantizedRoundedSignal signal, double targetSamplingFrequency);
}
