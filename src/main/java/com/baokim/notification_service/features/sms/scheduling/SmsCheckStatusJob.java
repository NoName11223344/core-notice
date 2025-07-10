package com.baokim.notification_service.features.sms.scheduling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@ConditionalOnProperty(name = "job.sms.check-status.enabled", havingValue = "true", matchIfMissing = true)
public class SmsCheckStatusJob {
    @Scheduled(cron = "${job.sms.check-status.cron}")
    public void checkSmsStatus() {
        log.info("SMS check-status job is running... ok");
    }
}