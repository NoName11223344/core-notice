package com.baokim.notification_service.commons.utils;

import java.text.Normalizer;

public class TextUtil {

    public static String removeUnicode(String text) {
        if (text.isEmpty()) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("đ", "d")
                .replaceAll("Đ", "D")
                .toLowerCase();
    }
}
