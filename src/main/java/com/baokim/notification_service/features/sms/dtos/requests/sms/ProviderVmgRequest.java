package com.baokim.notification_service.features.sms.dtos.requests.sms;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProviderVmgRequest {
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Định dạng requestId không hợp lệ")
    private String requestId;
    private String msisdn;

    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Định dạng referentId không hợp lệ")
    private String referentId;

    @Min(value = -2, message = "Status không hợp lệ")
    @Max(value = 2, message = "Status không hợp lệ")
    private Integer status;
}
