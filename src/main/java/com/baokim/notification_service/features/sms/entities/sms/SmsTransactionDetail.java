package com.baokim.notification_service.features.sms.entities.sms;

import com.baokim.notification_service.bases.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sms_transaction_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsTransactionDetail extends BaseEntity {

    @Column(name = "trans_id", length = 50, nullable = false)
    private String transId;

    @Column(name = "sent_to", length = 20, nullable = false)
    private String sentTo;

    @Column(name = "telco", length = 10)
    private String telco;

    @Column(name = "sub_reference_id")
    private String subReferenceId;

    @Column(name = "count_mt")
    private Byte countMt;

    @Column(name = "msg_length")
    private Integer msgLength;

    @Column(name = "content")
    private String content;

    @Column(name = "fee")
    private Integer fee;

    @Column(name = "total_fee")
    private Integer totalFee;

    @Column(name = "merchant_code")
    private String merchantCode;

    @Column(name = "response_code")
    private String responseCode;

    @Column(name = "status")
    private Byte status;
}