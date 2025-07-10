package com.baokim.notification_service.features.email.services;

import com.baokim.notification_service.features.email.configs.EmailConfig;
import com.baokim.notification_service.features.email.constants.EmailConstant;
import com.baokim.notification_service.features.email.services.strategy.EmailProvider;
import com.baokim.notification_service.features.email.services.strategy.MailgunEmailProvider;
import com.baokim.notification_service.features.email.services.strategy.SendGridEmailProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailProviderFactory {

    private final SendGridEmailProvider sendGridEmailProvider;
    private final MailgunEmailProvider mailgunEmailProvider;
    private final EmailConfig emailConfig;

    public EmailProvider getProvider() {
        switch (this.emailConfig.getProvider().toLowerCase()) {
            case EmailConstant.PROVIDER_SENDGRID:
                return sendGridEmailProvider;
            case EmailConstant.PROVIDER_MAILGUN:
                return mailgunEmailProvider;
            default:
                throw new IllegalArgumentException("Provider email không hợp lệ: " + this.emailConfig.getProvider());
        }
    }



}