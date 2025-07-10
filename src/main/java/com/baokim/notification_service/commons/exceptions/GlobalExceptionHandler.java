package com.baokim.notification_service.commons.exceptions;

import com.baokim.notification_service.bases.responses.ApiResponse;
import com.baokim.notification_service.commons.constants.ResponseConstants;
import com.baokim.notification_service.features.sms.validators.message.BlockedMessageValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException e) {

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.APP_EXCEPTION.getCode());

        apiResponse.setMessage(ErrorCode.APP_EXCEPTION.getMessage());

        log.info("Log RuntimeException return response {}", apiResponse);

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity <ApiResponse> handleAppException(AppException e) {

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(e.getErrorCode().getCode());

        apiResponse.setMessage(e.getErrorCode().getMessage());

        log.info("Log AppException return response {}", apiResponse);

        return ResponseEntity.status(e.getErrorCode().getHttpCode()).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity <ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.VALIDATION_ERROR.getCode());

        String errorMessage = "Invalid request";

        if (e.getFieldError() != null) {
            errorMessage = e.getFieldError().getDefaultMessage();
        } else if (e.getGlobalError() != null) { // cái này phục vụ validate templateId
            errorMessage = e.getGlobalError().getDefaultMessage();
        }

        if (errorMessage.equals(ErrorCode.MESS_BLOCKED_MESSAGE)) {
            apiResponse.setCode(ResponseConstants.BLOCKED_KEYWORD_CODE);
        }

        apiResponse.setMessage(errorMessage);

        log.info("Log MethodArgumentNotValidException return response {}", apiResponse);

        return ResponseEntity.badRequest().body(apiResponse);
    }

}
