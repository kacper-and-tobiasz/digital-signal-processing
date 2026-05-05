package org.kacperandtobiasz.model.util;

public class SignalUtil {
    public static boolean isNameValid(String name){
        return name != null && name.length() >= 3;
    }
}
