package com.baokim.notification_service.commons.utils;

import java.security.SecureRandom;
import java.time.Instant;

public class BkTransactionUtil {
    private static final String PREFIX = "BK";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    public static final int LENGTH = 20;

    public static String generateBkTransId(int length) {
        long timestamp = Instant.now().toEpochMilli(); // Lấy timestamp hiện tại (ms)
        String randomStr = genUniqueStr(PREFIX, length);

        return randomStr + timestamp;
    }

    private static String genUniqueStr(String prefix, int length) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
