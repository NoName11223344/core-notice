package com.baokim.notification_service.features.sms.entities.request_log;
import com.baokim.notification_service.bases.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "request_logs")
public class RequestLog extends BaseEntity {

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "request_time")
    private LocalDateTime requestTime;

    @Column(name = "service")
    private String service;

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "response_code")
    private String responseCode;

}
