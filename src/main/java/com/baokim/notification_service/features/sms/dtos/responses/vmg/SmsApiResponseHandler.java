package com.baokim.notification_service.features.sms.dtos.responses.vmg;

import com.baokim.notification_service.commons.exceptions.AppException;
import com.baokim.notification_service.commons.exceptions.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SmsApiResponseHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Xử lý JSON trả về kiểu đơn lẻ
    public SmsApiResponse handleSingleSmsResponse(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, SmsApiResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Lỗi convert single sms từ VMG json response: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.APP_EXCEPTION);
        }
    }

    // Xử lý JSON trả về kiểu array response [1 .., 2... ,3 ...]
    public Map<String, SmsApiResponse> handleMultiSmsResponse(String jsonResponse) {
        try {
            // Parse JSON thành List<Map<String, Object>>
            List<Map<String, Object>> rawList = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

            Map<String, SmsApiResponse> responses = new HashMap<>();

            for (int i = 0; i < rawList.size(); i++) {
                SmsApiResponse detail = objectMapper.convertValue(rawList.get(i), SmsApiResponse.class);
                responses.put(String.valueOf(i), detail);
            }

            return responses;
        } catch (JsonProcessingException e) {
            log.error("Lỗi convert multi sms từ VMG json response: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.APP_EXCEPTION);
        }
    }
}
