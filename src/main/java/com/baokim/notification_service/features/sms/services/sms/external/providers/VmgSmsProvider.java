package com.baokim.notification_service.features.sms.services.sms.external.providers;

import com.baokim.notification_service.features.sms.configs.SmsConfig;
import com.baokim.notification_service.features.sms.constants.SmsConstant;
import com.baokim.notification_service.features.sms.dtos.requests.sms.BaseSmsSendRequest;
import com.baokim.notification_service.features.sms.dtos.requests.vmg.SentOtpVmgRequest;
import com.baokim.notification_service.features.sms.dtos.requests.vmg.SentSmsBlockVmgRequest;
import com.baokim.notification_service.features.sms.dtos.requests.vmg.SentCskhVmgRequest;
import com.baokim.notification_service.commons.exceptions.AppException;
import com.baokim.notification_service.commons.exceptions.ErrorCode;
import com.baokim.notification_service.features.sms.services.request_log.RequestLogService;
import com.baokim.notification_service.features.sms.services.sms.external.SmsProvider;
import com.baokim.notification_service.commons.utils.DateTimeUtil;
import com.baokim.notification_service.integrations.googlechat.GoogleChatNotifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class VmgSmsProvider implements SmsProvider {

    private final SmsConfig smsConfig;
    private final OkHttpClient client;
    private final ModelMapper mapper;
    private final RequestLogService requestLogService;
    private final ObjectMapper objectMapper;
    private final GoogleChatNotifier googleChatNotifier;

    @Override
    public String sendOtp(BaseSmsSendRequest sentOtpRequest) {

        SentOtpVmgRequest vmgRequest = mapper.map(sentOtpRequest, SentOtpVmgRequest.class);

        vmgRequest.setScheduled(DateTimeUtil.convertScheduledDateFormat(sentOtpRequest.getScheduled()));

        String endPoint = "/SMSBrandname/SendOTP";

        return sendSmsRequest(vmgRequest, endPoint, vmgRequest.getRequestId());
    }

    @Override
    public String sendBlockSms(BaseSmsSendRequest sentBlockSmsRequest) {

        SentSmsBlockVmgRequest vmgRequest = mapper.map(sentBlockSmsRequest, SentSmsBlockVmgRequest.class);

        vmgRequest.setScheduled(DateTimeUtil.convertScheduledDateFormat(vmgRequest.getScheduled()));

        String endPoint = "/SMSBrandname/SendSMSBlock";

        return sendSmsRequest(vmgRequest, endPoint, vmgRequest.getRequestId());
    }

    @Override
    public String sendCskhSms(BaseSmsSendRequest sentCskhSmsRequest) {

        SentCskhVmgRequest smsVmgRequest = mapper.map(sentCskhSmsRequest, SentCskhVmgRequest.class);

        smsVmgRequest.setScheduled(DateTimeUtil.convertScheduledDateFormat(smsVmgRequest.getScheduled()));

        String endPoint = "/SMSBrandname/SendSMS";

        return sendSmsRequest(smsVmgRequest, endPoint, sentCskhSmsRequest.getRequestId());
    }

    public String sendSmsRequest(Object requestBody, String endPoint, String requestId) {
        try {
            SmsConfig.ProviderConfig providerConfig = smsConfig.getProviderConfig(SmsConstant.PROVIDER_VMG);
            if (providerConfig == null) {
                throw new RuntimeException("Provider VMG is not configured");
            }

            String url = providerConfig.getUrl() + endPoint;

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("token", providerConfig.getApiKey())
                    .post(body)
                    .build();

            log.info("Request VMG [{}] URL:{}, Body: {}", requestId, url, jsonBody);

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.info("Response VMG [{}] Unexpected code {} ", requestId, response);
                throw new AppException(ErrorCode.APP_EXCEPTION);
            }
            String responseBody = response.body() != null ? response.body().string() : "";

            log.info("Response VMG [{}], Body: {}", requestId, responseBody);

            requestLogService.saveRequestLogSmsFromProvider(
                    requestBody,
                    responseBody,
                    requestId,
                    DateTimeUtil.getCurrentDateTime(),
                    SmsConstant.PROVIDER_VMG.toUpperCase()
            );

            googleChatNotifier.sentNotifyIfErrorFromVmg(url, jsonBody, responseBody);

            return responseBody;
        } catch (IOException e) {
            log.error("Lỗi trong quá trình gọi VMG API [{}]: {}", requestId, e.getMessage());
            throw new AppException(ErrorCode.APP_EXCEPTION);
        }
    }
}
