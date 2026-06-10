package org.kacperandtobiasz.model.storage;

import java.io.*;

import org.kacperandtobiasz.model.base.signal.ComplexNumber;
import org.kacperandtobiasz.model.base.signal.ComplexSignal;
import org.kacperandtobiasz.model.base.signal.DiscreteSignal;
import org.kacperandtobiasz.model.base.signal.Signal;

public class SignalFileHandler {

    // Value type flag (0 - real, 1 - complex)
    private static final byte VALUE_TYPE_REAL = 0;
    private static final byte VALUE_TYPE_COMPLEX = 1;

    public void saveToBinaryFile(Signal signal, File file) throws IOException {
        if (signal.hasComplexSignal()) {
            saveToBinaryFile(signal.getComplexSignal(), file);
        } else {
            saveToBinaryFile(signal.getDiscreteSignal(), file);
        }
    }

    public void saveToBinaryFile(DiscreteSignal signal, File file) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            // Initial time (t1) - double 
            dos.writeDouble(signal.startTime());
            
            // Sampling frequency - double
            dos.writeDouble(signal.samplingFrequency());
            
            // Value type - byte (0 for real)
            dos.writeByte(VALUE_TYPE_REAL);
            
            // Sample count - int
            dos.writeInt(signal.samples().length);
            
            // Amplitudes - double arrays
            for (double sample : signal.samples()) {
                dos.writeDouble(sample);
            }
        }
    }

    public DiscreteSignal loadFromBinaryFile(File file) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            double startTime = dis.readDouble();
            double samplingFreq = dis.readDouble();
            byte valueType = dis.readByte();
            int samplesCount = dis.readInt();
            
            if (valueType != VALUE_TYPE_REAL) {
                throw new UnsupportedOperationException("Only real values (0) are supported by this method.");
            }
            
            double[] samples = new double[samplesCount];
            for (int i = 0; i < samplesCount; i++) {
                samples[i] = dis.readDouble();
            }
            
            return new DiscreteSignal(samples, samplingFreq, startTime);
        }
    }

    public void saveToBinaryFile(ComplexSignal signal, File file) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            dos.writeDouble(signal.startFrequency());
            dos.writeDouble(signal.samplingFrequency());
            dos.writeByte(VALUE_TYPE_COMPLEX);
            dos.writeInt(signal.samples().length);
            for (ComplexNumber sample : signal.samples()) {
                dos.writeDouble(sample.real());
                dos.writeDouble(sample.imaginary());
            }
        }
    }

    public Signal loadSignalFromBinaryFile(File file, String signalName) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            double start = dis.readDouble();
            double samplingFreq = dis.readDouble();
            byte valueType = dis.readByte();
            int samplesCount = dis.readInt();

            if (valueType == VALUE_TYPE_REAL) {
                double[] samples = new double[samplesCount];
                for (int i = 0; i < samplesCount; i++) {
                    samples[i] = dis.readDouble();
                }
                return new Signal(signalName, new DiscreteSignal(samples, samplingFreq, start));
            }
            if (valueType == VALUE_TYPE_COMPLEX) {
                ComplexNumber[] samples = new ComplexNumber[samplesCount];
                for (int i = 0; i < samplesCount; i++) {
                    samples[i] = new ComplexNumber(dis.readDouble(), dis.readDouble());
                }
                return new Signal(signalName, new ComplexSignal(samples, samplingFreq, start));
            }
            throw new UnsupportedOperationException("Unsupported value type: " + valueType);
        }
    }

    // Required for textual presentation of file data
    public String generateTextPreview(File file, int maxSamplesToShow) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            double startTime = dis.readDouble();
            double samplingFreq = dis.readDouble();
            byte valueType = dis.readByte();
            int samplesCount = dis.readInt();
            
            sb.append("--- File Parameters ---\n")
              .append("Start time (t1): ").append(startTime).append(" s\n")
              .append("Sampling frequency: ").append(samplingFreq).append(" Hz\n")
              .append("Value type: ").append(valueType == 0 ? "Real" : "Complex").append("\n")
              .append("Sample count: ").append(samplesCount).append("\n\n")
              .append("--- First Samples ---\n");
            
            int samplesToRead = Math.min(samplesCount, maxSamplesToShow);
            for(int i = 0; i < samplesToRead; i++) {
                if (valueType == VALUE_TYPE_COMPLEX) {
                    double real = dis.readDouble();
                    double imaginary = dis.readDouble();
                    sb.append("Sample [").append(i).append("]: ").append(real).append(" + ").append(imaginary).append("i\n");
                } else {
                    double val = dis.readDouble();
                    sb.append("Sample [").append(i).append("]: ").append(val).append("\n");
                }
            }
            
            if (samplesCount > maxSamplesToShow) {
                sb.append("... and ").append(samplesCount - maxSamplesToShow).append(" more.\n");
            }
        }
        return sb.toString();
    }
}
