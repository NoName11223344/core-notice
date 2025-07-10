package com.baokim.notification_service.bases.responses;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class ErrorResponseFilter {

    public static void sentErrorFilterResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setCode(status.value());
        apiResponse.setMessage(message);

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
