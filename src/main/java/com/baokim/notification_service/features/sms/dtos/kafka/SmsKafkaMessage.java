package com.baokim.notification_service.features.sms.dtos.kafka;

import com.baokim.notification_service.features.sms.dtos.requests.sms.BaseSmsSendRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class SmsKafkaMessage implements Serializable {
    private BaseSmsSendRequest request;
    private String tranId;

}
