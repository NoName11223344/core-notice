package com.baokim.notification_service.features.email.dtos.kafka;


import lombok.Data;

import java.io.Serializable;

@Data
public class EmailKafkaMessage implements Serializable{
        private String requestId;
        private Long emailId;

}
