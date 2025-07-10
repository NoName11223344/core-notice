package com.baokim.notification_service.commons.filters;

import com.baokim.notification_service.bases.responses.ApiResponse;
import com.baokim.notification_service.bases.responses.ErrorResponseFilter;
import com.baokim.notification_service.features.sms.constants.MerchantConstant;
import com.baokim.notification_service.features.sms.entities.merchant.Merchant;
import com.baokim.notification_service.features.sms.repositories.merchant.MerchantRepository;
import com.baokim.notification_service.commons.utils.SignUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignFilter extends OncePerRequestFilter {

    private final MerchantRepository merchantRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);

        JsonNode jsonNode = objectMapper.readTree(cachedRequest.getInputStream());
        String merchantCode = jsonNode.path("merchantCode").asText();

        request.setAttribute("merchantCode", merchantCode);

        String plainText = objectMapper.writeValueAsString(jsonNode);
        log.info("Request gửi SMS from merchant: {}", plainText);

        // Bypass nếu là VMG
        if ("VMG".equals(merchantCode)) {
            filterChain.doFilter(cachedRequest, response);
            return;
        }

        String signature = request.getHeader("Signature");

        if (merchantCode == null || merchantCode.isEmpty()) {
            log.info("Request gửi lên không có merchantCode");
            sendErrorResponse(response, HttpStatus.BAD_REQUEST, "MerchantCode không được để trống");
            return;
        }

        Merchant merchant = merchantRepository.findByMerchantCode(merchantCode);

        if (merchant == null) {
            log.info("Không tìm thấy merchant {} gửi lên ở request filter để kiểm tra chữ ký", merchantCode);
            sendErrorResponse(response, HttpStatus.BAD_REQUEST, "Merchant không hợp lệ");
            return;
        }

        if (!MerchantConstant.STATUS_ACTIVE.equals(merchant.getStatus())) {
            log.info("Merchant đang không hoạt động {}", merchantCode);
            sendErrorResponse(response, HttpStatus.BAD_REQUEST, "Merchant đang không hoạt động");
            return;
        }

        if (merchant.getPublicKey() == null) {
            log.info("Merchant chưa được cấu hình key: {}", merchantCode);
            sendErrorResponse(response, HttpStatus.BAD_REQUEST, "Chưa được cấu hình key cho merchant");
            return;
        }

        log.info("Merchant code = {}, chữ ký gửi lên: {}", merchantCode, signature);

        if (signature == null || !SignUtil.verifySignature(plainText, signature, merchant.getPublicKey())) {
            log.info("Chữ ký không hợp lệ. Merchant code = {}, signature = {}", merchantCode, signature);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Chữ ký không hợp lệ");
            return;
        }

        filterChain.doFilter(cachedRequest, response);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        ErrorResponseFilter.sentErrorFilterResponse(response, status, message);
    }
}

