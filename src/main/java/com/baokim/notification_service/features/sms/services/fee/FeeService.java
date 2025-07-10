package com.baokim.notification_service.features.sms.services.fee;

import com.baokim.notification_service.features.sms.entities.fee.TemFeeDetail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeeService {

    public int getFee(List<TemFeeDetail> tempFees, int fieldId, String telco, int smsType) {
        for (TemFeeDetail fee : tempFees) {
            if (feeMatchesFieldTelcoType(fee, fieldId, telco, smsType)) {
                return fee.getFee();
            }
        }
        return 0;
    }

    private boolean feeMatchesFieldTelcoType(TemFeeDetail fee, int fieldId, String telco, int smsType) {
        return fee.getCategoryId() == fieldId &&
                fee.getTelco().equalsIgnoreCase(telco) &&
                fee.getType() == smsType;
    }

}
