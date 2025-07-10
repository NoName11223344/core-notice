package com.baokim.notification_service.features.sms.filters;

import com.baokim.notification_service.bases.responses.ErrorResponseFilter;
import com.baokim.notification_service.features.sms.constants.MerchantConstant;
import com.baokim.notification_service.features.sms.constants.SmsConstant;
import com.baokim.notification_service.features.sms.entities.merchant.Merchant;
import com.baokim.notification_service.features.sms.repositories.merchant.MerchantRepository;
import com.baokim.notification_service.features.sms.repositories.sms.SmsTransactionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsLimitFilter extends OncePerRequestFilter {
    private final MerchantRepository merchantRepository;
    private final SmsTransactionRepository smsTransactionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String merchantCode = (String) request.getAttribute("merchantCode");

        Merchant merchant = merchantRepository.findByMerchantCode(merchantCode);

        if(! MerchantConstant.ALLOWED.equals(merchant.getIsSentSms())) {
            log.info("Merchant {} này đang không cho phép gửi sms", merchantCode);
            sendErrorResponse(response, HttpStatus.BAD_REQUEST, "Merchant đang không cho phép gửi tin nhắn");
            return;
        }

        if (merchant.getSmsLimitInDay() != null && merchant.getSmsLimitInDay() > 0) {
            Integer totalMerchantSentInDay = smsTransactionRepository.countTransactionsTodayByMerchantAndStatus(
                    merchantCode,
                    SmsConstant.SENT_MESS_SUCCESS,
                    LocalDate.now().atStartOfDay(),
                    LocalDateTime.now()
            );
            if (totalMerchantSentInDay >= merchant.getSmsLimitInDay()) {
                log.info("Merchant {} đã quá số lượt tin {} gửi trong ngay:", merchantCode, totalMerchantSentInDay);
                sendErrorResponse(response, HttpStatus.BAD_REQUEST, "Bạn đã quá số lượng gửi tin nhắn trong ngày");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        ErrorResponseFilter.sentErrorFilterResponse(response, status, message);
    }

}
