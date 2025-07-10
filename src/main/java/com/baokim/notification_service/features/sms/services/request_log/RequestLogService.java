package com.baokim.notification_service.features.sms.services.request_log;

import com.baokim.notification_service.features.email.dtos.email_log.EmailRequestLogDTO;
import com.baokim.notification_service.features.email.dtos.requests.EmailRequest;
import com.baokim.notification_service.features.sms.dtos.requests.sms.BaseSmsSendRequest;
import com.baokim.notification_service.bases.responses.ApiResponse;
import com.baokim.notification_service.features.sms.entities.request_log.RequestLog;
import com.baokim.notification_service.commons.exceptions.AppException;
import com.baokim.notification_service.commons.exceptions.ErrorCode;
import com.baokim.notification_service.features.sms.repositories.request_log.RequestLogRepository;
import com.baokim.notification_service.commons.utils.DateTimeUtil;
import com.baokim.notification_service.commons.utils.HelperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestLogService {

    private final ObjectMapper objectMapper;
    private final RequestLogRepository requestLogRepository;

    public void saveRequestLogSmsFromMerchant(BaseSmsSendRequest request, ApiResponse response) {
       try {
           RequestLog requestLog = RequestLog.builder()
                   .requestId(request.getRequestId())
                   .requestTime(DateTimeUtil.convertLocalDateTime(request.getRequestTime()))
                   .service(request.getMerchantCode())
                   .requestBody(objectMapper.writeValueAsString(request))
                   .responseBody(objectMapper.writeValueAsString(response))
                   .responseCode(String.valueOf(response.getCode()))
                   .build();

           requestLogRepository.save(requestLog);
       } catch (Exception e) {
           log.error("Không lưu được log request gửi sms from merchant ", e);
           throw new AppException(ErrorCode.APP_EXCEPTION);
       }
    }

    public void saveRequestLogSmsFromProvider(Object request, String response, String requestId, String requestTime, String merchantCode) {
        try {
            RequestLog requestLog = RequestLog.builder()
                    .requestId(requestId)
                    .requestTime(DateTimeUtil.convertLocalDateTime(requestTime))
                    .service(merchantCode)
                    .requestBody(objectMapper.writeValueAsString(request))
                    .responseBody(response)
                    .responseCode(HelperUtil.getErrorCodeFromJson(response))
                    .build();

            requestLogRepository.save(requestLog);
        } catch (Exception e) {
            log.error("Không lưu được log request gửi sms from provider ", e);
            throw new AppException(ErrorCode.APP_EXCEPTION);
        }
    }

    public void saveRequestLogEmail(EmailRequest emailRequest, ApiResponse response) {
        try {

            EmailRequestLogDTO logDto = EmailRequestLogDTO.builder()
                    .subject(emailRequest.getSubject())
                    .cc(emailRequest.getCc())
                    .to(emailRequest.getTo())
                    .from(emailRequest.getFromName())
                    .merchantCode(emailRequest.getMerchantCode())
                    .requestId(emailRequest.getRequestId())
                    .requestTime(emailRequest.getRequestTime())
                    .contentType(emailRequest.getContentType())
                    .build();

            RequestLog requestLog = RequestLog.builder()
                    .requestId(emailRequest.getRequestId())
                    .requestTime(DateTimeUtil.convertLocalDateTime(emailRequest.getRequestTime()))
                    .service(emailRequest.getMerchantCode())
                    .requestBody(objectMapper.writeValueAsString(logDto))
                    .responseBody(objectMapper.writeValueAsString(response))
                    .responseCode(String.valueOf(response.getCode()))
                    .build();

            requestLogRepository.save(requestLog);
        } catch (Exception e) {
            log.error("Không lưu được log request gửi sms from provider ", e);
            throw new AppException(ErrorCode.APP_EXCEPTION);
        }
    }

}
