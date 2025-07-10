package com.baokim.notification_service.features.sms.controllers;

import com.baokim.notification_service.features.sms.dtos.requests.sms.ProviderVmgRequest;
import com.baokim.notification_service.bases.responses.ApiResponse;
import com.baokim.notification_service.features.sms.services.sms.internal.WebhookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/api/sms/callback-status")
    public ApiResponse updateStatus(@RequestBody @Valid ProviderVmgRequest request) {
       return webhookService.updateStatus(request);
    }
}
