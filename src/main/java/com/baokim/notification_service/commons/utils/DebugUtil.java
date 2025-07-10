package com.baokim.notification_service.commons.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class DebugUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .registerModule(new JavaTimeModule());

    public static void dump(Object... values) {
        try {
            for (Object value : values) {
                if (value instanceof String || value instanceof Number || value.getClass().isPrimitive()) {
                    System.out.println(value);
                } else {
                    String json = objectMapper.writeValueAsString(value);
                    System.out.println(json);
                }
            }
        } catch (Exception e) {
            System.err.println("Error while dumping data: " + e.getMessage());
        }
    }

    public static void dd(Object... values) {
        dump(values);
        System.exit(1);
    }
}
