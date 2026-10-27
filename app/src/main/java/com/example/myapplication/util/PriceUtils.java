package com.example.myapplication.util;

import java.util.Locale;

/**
 * 金额格式化工具类：29.0 -> "29"，29.5 -> "29.5"，29.55 -> "29.55"
 */
public class PriceUtils {

    public static String fmt(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        String s = String.format(Locale.CHINA, "%.2f", value);
        s = s.replaceAll("0+$", "");
        s = s.replaceAll("\\.$", "");
        return s;
    }
}
