package com.baokim.notification_service.integrations.googlechat;


import com.baokim.notification_service.features.sms.dtos.responses.vmg.SmsApiResponse;
import com.baokim.notification_service.features.sms.dtos.responses.vmg.SmsApiResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleChatNotifier {

    private final ObjectMapper objectMapper;
    private final OkHttpClient client;
    private final SmsApiResponseHandler smsApiResponseHandler;


    @Value("${google.chat.webhook_url}")
    private String webhookUrl;

    public void sendMessage(String message) {
        try {
            Map<String, String> messagePayload = new HashMap<>();
            messagePayload.put("text", message);

            String jsonBody = objectMapper.writeValueAsString(messagePayload);

            RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(webhookUrl)
                    .post(body)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to send message to Google Chat. Response code: {}", response.code());
                }
            }
        } catch (IOException e) {
            log.error("Error sending message to Google Chat: {}", e.getMessage());
        }
    }

    public void sentResponseError(String title, String url, String jsonBody, String responseBody)  {
       try {
           String errorMessage = "🚨 " + title + "\n" +
                   "- URL: " + url + "\n" +
                   "- Request Body: " + jsonBody + "\n" +
                   "- Response Body: " + responseBody;
           sendMessage(errorMessage);
       } catch (Exception e) {
           log.error("Error sentResponseError message to Google Chat: {}", e.getMessage());
       }
    }

    public void sentNotifyIfErrorFromVmg(String url, String jsonBody, String responseBody){

        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            if (jsonNode.isArray()) {
                Map<String, SmsApiResponse> responses = smsApiResponseHandler.handleMultiSmsResponse(responseBody);
                boolean hasAtLeastOneMessageFailed = responses.values().stream()
                        .anyMatch(response -> !SmsApiResponse.isSmsSendSuccess(response.getErrorCode()));

                if (hasAtLeastOneMessageFailed) {
                    sentResponseError("Gửi SMS Block có tin nhắn không thành công, kiểm tra lại", url, jsonBody, responseBody);
                }
            } else {
                SmsApiResponse smsApiResponse = smsApiResponseHandler.handleSingleSmsResponse(responseBody);
                if (!SmsApiResponse.isSmsSendSuccess(smsApiResponse.getErrorCode()))  {
                    sentResponseError("Gửi SMS OTP, CSKh sang VMG bị lỗi, kiểm tra lại", url, jsonBody, responseBody);
                }
            }
        } catch (Exception e) {
            log.error("Error sentNotifyErrorFromVmg message to Google Chat: {}", e.getMessage());
        }
    }
}
