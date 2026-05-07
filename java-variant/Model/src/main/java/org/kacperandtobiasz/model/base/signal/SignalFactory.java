package org.kacperandtobiasz.model.base.signal;

import org.kacperandtobiasz.model.base.signal.generator.GeneratorFactory;
import org.kacperandtobiasz.model.base.signal.generator.SignalGenerator;

public class SignalFactory {

    public static Signal create(
            SignalType type,
            String name,
            double samplingFrequency,
            SignalParameters params
    ) {
        SignalGenerator generator = GeneratorFactory.create(type, samplingFrequency, params);
        return Signal.sampled(name, generator, samplingFrequency);
    }
}
