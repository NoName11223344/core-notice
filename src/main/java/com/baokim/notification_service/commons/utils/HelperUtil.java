package com.baokim.notification_service.commons.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;

public class HelperUtil {

    public static String formatHeadPhone(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.replaceFirst("^0", "84");
    }

    public static String getErrorCodeFromJson(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            return rootNode.path("errorCode").asText();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean hasDuplicateEmails(String toEmails, String ccEmails) {
        String[] toEmailArray = toEmails.split(",");
        String[] ccEmailArray = ccEmails.split(",");

        Set<String> emailSet = new HashSet<>();

        // Thêm các email từ "to"
        for (String email : toEmailArray) {
            emailSet.add(email.trim());
        }

        // Thêm các email từ "cc" và kiểm tra trùng
        for (String email : ccEmailArray) {
            if (!emailSet.add(email.trim())) {  // Nếu không thể thêm, tức là đã trùng
                return true;
            }
        }

        return false;
    }

}
