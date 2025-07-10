package com.baokim.notification_service.commons.exceptions;

import com.baokim.notification_service.commons.constants.ResponseConstants;

public enum ErrorCode {
    APP_EXCEPTION(ResponseConstants.ERROR_CODE , "Lỗi hệ thống, vui lòng thử lại", ResponseConstants.ERROR_CODE),
    VALIDATION_ERROR(ResponseConstants.CODE_FAIL_VALIDATE, "Tham số không hợp lệ kiểu dữ liệu", ResponseConstants.HTTP_ERROR_VALIDATE),
    TRANSACTION_NOTFOUND(ResponseConstants.CODE_FAIL_VALIDATE, "Không tìm thấy giao dịch", ResponseConstants.HTTP_ERROR_VALIDATE),
    TEMPLATE_REGEX_INVALID(ResponseConstants.CODE_FAIL_VALIDATE, "Nội dung tin nhắn không khớp với template.", ResponseConstants.HTTP_ERROR_VALIDATE),
    TEMPLATE_REGEX_NOTFOUND(ResponseConstants.CODE_FAIL_VALIDATE, "Không tìm thấy template message.", ResponseConstants.HTTP_ERROR_VALIDATE),
    BLOCKED_MESSAGE(ResponseConstants.BLOCKED_KEYWORD_CODE , "Nội dung tin nhắn có chứa từ khoá bị block", ResponseConstants.HTTP_ERROR_VALIDATE),
    DUPLICATE_EMAIL(ResponseConstants.CODE_FAIL_VALIDATE , "Email bị duplicate ở email to và email cc", ResponseConstants.HTTP_ERROR_VALIDATE)
    ;

    public static final String MESS_BLOCKED_MESSAGE = "Nội dung tin nhắn có chứa từ khoá bị block";


    private int code;
    private String message;
    private int http_code;

    ErrorCode(int code, String message, int http_code) {
        this.code = code;
        this.message = message;
        this.http_code = http_code;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpCode() {
        return http_code;
    }

}
