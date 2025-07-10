package com.baokim.notification_service.features.sms.services.sms.external;

import com.baokim.notification_service.features.sms.configs.SmsConfig;
import com.baokim.notification_service.features.sms.constants.SmsConstant;
import com.baokim.notification_service.features.sms.services.sms.external.providers.VmgSmsProvider;
import org.springframework.stereotype.Service;

@Service
public class SmsProviderFactory {
    private final SmsConfig smsConfig;
    private final VmgSmsProvider vmgSmsProvider;

    public SmsProviderFactory(
            SmsConfig smsConfig,
            VmgSmsProvider vmgSmsProvider
    ) {
        this.vmgSmsProvider = vmgSmsProvider;
        this.smsConfig = smsConfig;
    }

    public SmsProvider getProvider() {
        switch (this.smsConfig.getProvider().toLowerCase()) {
            case SmsConstant.PROVIDER_VMG:
                return vmgSmsProvider;
            default:
                throw new IllegalArgumentException("Provider không hợp lệ: " + this.smsConfig.getProvider());
        }
    }
}
