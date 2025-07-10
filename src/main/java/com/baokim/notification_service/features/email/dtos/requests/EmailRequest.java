package com.baokim.notification_service.features.email.dtos.requests;

import com.baokim.notification_service.features.email.validators.email.ValidCommaSeparatedEmails;
import com.baokim.notification_service.features.email.validators.file.ValidFile;
import com.baokim.notification_service.features.sms.validators.merchant_code.ValidMerchantCode;
import com.baokim.notification_service.features.sms.validators.request_id.UniqueRequestId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Định dạng request id không hợp lệ")
    @Size(max = 100, message = "Request id không được quá 100 kí tự")
    @UniqueRequestId
    private String requestId;

    @NotBlank(message = "Request time không hợp lệ")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "Định dạng request time không hợp lệ")
    private String requestTime;

    @NotBlank(message = "Mã merchant không hợp lệ")
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Mã merchant không hợp lệ")
    @ValidMerchantCode
    private String merchantCode;

    @NotBlank(message = "From name không hợp lệ")
    private String fromName;

    @ValidCommaSeparatedEmails
    private String fromEmail;

    @NotEmpty(message = "Danh sách người nhận không được để trống")
    @ValidCommaSeparatedEmails
    private String to;

    @NotBlank(message = "Subject không hợp lệ")
    private String subject;

    @NotBlank(message = "Content không hợp lệ")
    private String content;

    @NotNull(message = "ContentType không hợp lệ")
    @Min(value = 1, message = "Định dạng contentType không hợp lệ")
    @Max(value = 2, message = "Định dạng contentType không hợp lệ")
    private Byte contentType;

    @ValidCommaSeparatedEmails
    private String cc;

    @Valid
    private List<@ValidFile MultipartFile> files;
}
