package zcla71.utils;

import java.text.Normalizer;

public class StringUtils {
    public static String removeAcentos(String str) {
        String result = Normalizer.normalize(str, Normalizer.Form.NFD);
        result = result.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return result;
    }
}
