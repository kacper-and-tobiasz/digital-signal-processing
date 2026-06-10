package org.kacperandtobiasz.model.base.signal;

public record ComplexNumber(double real, double imaginary) {
    public ComplexNumber add(ComplexNumber other) {
        return new ComplexNumber(real + other.real, imaginary + other.imaginary);
    }

    public ComplexNumber subtract(ComplexNumber other) {
        return new ComplexNumber(real - other.real, imaginary - other.imaginary);
    }

    public ComplexNumber multiply(ComplexNumber other) {
        return new ComplexNumber(
                real * other.real - imaginary * other.imaginary,
                real * other.imaginary + imaginary * other.real
        );
    }

    public ComplexNumber multiply(double value) {
        return new ComplexNumber(real * value, imaginary * value);
    }

    public double magnitude() {
        return Math.hypot(real, imaginary);
    }

    public double phase() {
        return Math.atan2(imaginary, real);
    }
}
