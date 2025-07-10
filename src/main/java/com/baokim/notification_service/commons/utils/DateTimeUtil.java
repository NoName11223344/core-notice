package com.baokim.notification_service.commons.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final String COMMON_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static LocalDateTime convertLocalDateTime(String dateString) {

        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(COMMON_DATETIME_FORMAT);

        return LocalDateTime.parse(dateString, formatter);
    }

    public static String getCurrentDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter);
    }

    public static String convertScheduledDateFormat(String inputDate) {

        if (inputDate == null || inputDate.trim().isEmpty()) {
            return "";
        }

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        LocalDateTime dateTime = LocalDateTime.parse(inputDate, inputFormatter);

        return dateTime.format(outputFormatter);
    }


}
