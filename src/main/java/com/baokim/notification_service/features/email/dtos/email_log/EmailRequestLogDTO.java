package com.baokim.notification_service.features.email.dtos.email_log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailRequestLogDTO {
    private String requestId;
    private String requestTime;
    private String from;
    private String subject;
    private String to;
    private String cc;
    private Byte contentType;
    private String merchantCode;
}
