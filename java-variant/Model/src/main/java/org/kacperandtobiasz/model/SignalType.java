package org.kacperandtobiasz.model;

public enum SignalType {
    UNIFORM_NOISE("(S1) szum o rozkładzie jednostajnym"),
    GAUSS_NOISE("(S2) szum gaussowski"),
    SIN("(S3) sygnał sinusoidalny"),
    SIN_HALF_RECT("(S4) sygnał sinusoidalny wyprostowany jednopołówkowo"),
    SIN_FULL_RECT("(S5) sygnał sinusoidalny wyprostowany dwupołówkowo"),
    RECT("(S6) sygnał prostokątny"),
    RECT_SYMMETRIC("(S7) sygnał prostokątny symetryczny"),
    TRIAN("(S8) sygnał trójkątny"),
    UNIT_JUMP("(S9) skok jednostkowy"),
    UNIT_IMPULSE("(S10) impuls jednostkowy"),
    IMPULSE_NOISE("(S11) szum impulsowy");


    private final String displayName;

    SignalType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName; // This is what the ComboBox shows to the user
    }
}