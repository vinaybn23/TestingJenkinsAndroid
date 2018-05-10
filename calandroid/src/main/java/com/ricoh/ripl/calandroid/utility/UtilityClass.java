package com.ricoh.ripl.calandroid.utility;

/**
 * Created by vinaybn on 3/26/18.
 */

public final class UtilityClass {
    public static String convertFloatToString(float floatValue) {
        String convertedString = Float.toString(floatValue);
        convertedString = convertedString.replace(".", "");
        return convertedString;
    }
}
