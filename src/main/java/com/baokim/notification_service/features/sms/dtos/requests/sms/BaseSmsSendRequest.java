package com.baokim.notification_service.features.sms.dtos.requests.sms;

import com.baokim.notification_service.features.sms.validators.brandname.ValidBrandName;
import com.baokim.notification_service.features.sms.validators.merchant_code.ValidMerchantCode;
import com.baokim.notification_service.features.sms.validators.message.CheckBlockedMessage;
import com.baokim.notification_service.features.sms.validators.phone.ValidPhoneNumbers;
import com.baokim.notification_service.features.sms.validators.reference_id_partner.UniqueReferenceId;
import com.baokim.notification_service.features.sms.validators.request_id.UniqueRequestId;
import com.baokim.notification_service.features.sms.validators.scheduled.ValidScheduled;
import com.baokim.notification_service.features.sms.validators.template_id.ValidTemplateId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor

@Schema(description = "Thông tin gửi Sms")
@ValidTemplateId
public class BaseSmsSendRequest {

    @Schema(hidden = true)
    @Valid
    private CskhSmsSendRequest cskhSmsSendRequest;

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

    @NotBlank(message = "Mã đối tác không hợp lệ")
    @Size(max = 255, message = "Mã đối tác không được quá 255 kí tự")
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Mã đối tác không hợp lệ")
    @UniqueReferenceId
    private String referenceIdPartner;

    @Schema(hidden = true)
    protected Integer operation;

    @NotBlank(message = "Số điện thoại không được bỏ trống")
    @ValidPhoneNumbers
    private String to;

    @NotNull(message = "Định dạng type không được bỏ trống")
    @Min(value = 1, message = "Định dạng type phải là 1, 2")
    @Max(value = 2, message = "Định dạng type phải là 1, 2")
    private Byte type;

    @NotBlank(message = "Brandname không hợp lệ")
    @Size(max = 255, message = "Brandname không được quá 255 kí tự")
    @Pattern(regexp = "^[a-zA-Z0-9-_. ]+$", message = "Brandname không hợp lệ")
    @ValidBrandName
    private String from;

    @NotBlank(message = "Tin nhắn không được bỏ trống")
    @Size(max = 255, message = "Tin nhắn không được quá 255 kí tự")
    @CheckBlockedMessage
    private String message;

    @ValidScheduled
    private String scheduled;

    @Min(value = 0, message = "Định dạng unicode phải là 0, 1, 2")
    @Max(value = 2, message = "Định dạng unicode phải là 0, 1, 2")
    private Byte useUnicode;

    private Long templateId;

}
