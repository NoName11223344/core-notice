package com.baokim.notification_service.features.sms.services.sms.internal.strategy;

import com.baokim.notification_service.commons.utils.EncryptionUtil;
import com.baokim.notification_service.features.sms.configs.SmsConfig;
import com.baokim.notification_service.features.sms.constants.SmsConstant;
import com.baokim.notification_service.features.sms.constants.TempFeeConstant;
import com.baokim.notification_service.features.sms.dtos.requests.sms.BaseSmsSendRequest;
import com.baokim.notification_service.features.sms.dtos.responses.vmg.SmsApiResponse;
import com.baokim.notification_service.features.sms.dtos.responses.vmg.SmsApiResponseHandler;
import com.baokim.notification_service.features.sms.entities.fee.TemFeeDetail;
import com.baokim.notification_service.features.sms.entities.merchant.Merchant;
import com.baokim.notification_service.features.sms.entities.sms.SmsTransaction;
import com.baokim.notification_service.features.sms.entities.sms.SmsTransactionDetail;
import com.baokim.notification_service.commons.exceptions.AppException;
import com.baokim.notification_service.commons.exceptions.ErrorCode;
import com.baokim.notification_service.features.sms.repositories.fee.TempFeeDetailRepository;
import com.baokim.notification_service.features.sms.repositories.merchant.MerchantRepository;
import com.baokim.notification_service.features.sms.repositories.sms.SmsTransactionDetailRepository;
import com.baokim.notification_service.features.sms.repositories.sms.SmsTransactionRepository;
import com.baokim.notification_service.features.sms.services.fee.FeeService;
import com.baokim.notification_service.features.sms.services.sms.external.SmsProvider;
import com.baokim.notification_service.features.sms.services.sms.external.SmsProviderFactory;
import com.baokim.notification_service.commons.utils.DateTimeUtil;
import com.baokim.notification_service.commons.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BlockSmsProcessStrategy implements SmsProcessStrategy {

    private final SmsConfig smsConfig;
    private final SmsTransactionRepository smsTransactionRepository;
    private final SmsTransactionDetailRepository smsTransactionDetailRepository;
    private final SmsProviderFactory smsProviderFactory;
    private final SmsApiResponseHandler smsApiResponseHandler;
    private final MerchantRepository merchantRepository;
    private final TempFeeDetailRepository tempFeeDetailRepository;
    private final FeeService feeService;

    @Override
    public SmsTransaction createSmsTransaction(BaseSmsSendRequest request, String tranId) {
        SmsTransaction smsTransaction = SmsTransaction.builder()
                .transId(tranId)
                .requestId(request.getRequestId())
                .requestTime(DateTimeUtil.convertLocalDateTime(request.getRequestTime()))
                .referenceIdPartner(request.getReferenceIdPartner())
                .merchantCode(request.getMerchantCode())
                .brandname(request.getFrom())
                .providerCode(smsConfig.getProvider().toUpperCase())
                .sentType(SmsConstant.SENT_TO_MANY)
                .smsType(SmsConstant.SMS_TYPE_CUSTOMER_CARE_BLOCK)
                .scheduled(request.getScheduled() != null ? DateTimeUtil.convertLocalDateTime(request.getScheduled()) : null)
                .status(SmsConstant.SENT_MESS_PENDING)
                .templateId(request.getTemplateId())
                .build();

        return smsTransactionRepository.save(smsTransaction);
    }

    @Override
    public SmsTransactionDetail createSmsTransactionDetail(BaseSmsSendRequest request, String tranId) {

        List<SmsTransactionDetail> detailsList = new ArrayList<>();

        String[] phones = request.getTo().split(",");

        for (String phone : phones) {
            SmsTransactionDetail detail = getDataDetailTransaction(tranId, phone, request);
            detailsList.add(detail);
        }

        return smsTransactionDetailRepository.saveAll(detailsList).get(0);
    }

    @Override
    public String sendSms(BaseSmsSendRequest request) {

        SmsProvider provider = smsProviderFactory.getProvider();

        return provider.sendBlockSms(request);
    }

    private SmsTransactionDetail getDataDetailTransaction(String tranId, String phone, BaseSmsSendRequest request) {
        return SmsTransactionDetail.builder()
              .transId(tranId)
              .sentTo(HelperUtil.formatHeadPhone(phone))
              .content(EncryptionUtil.encrypt(request.getMessage()))
              .merchantCode(request.getMerchantCode())
              .status(SmsConstant.SENT_MESS_PENDING)
              .build();
    }

    @Override
    public SmsTransaction updateSmsTransaction(String responseProvider, String tranId) {

        SmsTransaction transaction = smsTransactionRepository.findByTransId(tranId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOTFOUND));

        Map<String, SmsApiResponse> responses = smsApiResponseHandler.handleMultiSmsResponse(responseProvider);

        boolean hasSuccess = hasAnySuccessResponse(responses);

        Byte status = hasSuccess ? SmsConstant.SENT_MESS_SUCCESS : SmsConstant.SENT_MESS_FAIL;
        transaction.setStatus(status);

        if (hasSuccess) {
            transaction.setResponseCode(SmsApiResponse.SUCCESS_CODE);
        } else {
            transaction.setResponseCode(getAnyErrorCode(responses));
        }

        return smsTransactionRepository.save(transaction);
    }

    @Override
    public SmsTransactionDetail updateSmsTransactionDetail(BaseSmsSendRequest request, String responseProvider, String tranId) {

        Map<String, SmsApiResponse> responses = smsApiResponseHandler.handleMultiSmsResponse(responseProvider);

        Merchant merchant = merchantRepository.findByMerchantCode(request.getMerchantCode());

        List<TemFeeDetail> tempDetails = tempFeeDetailRepository.findAllByTempCodeAndStatus(
                merchant.getTempFeeCode(),
                TempFeeConstant.TEMP_ACTIVE
        );

        for (SmsApiResponse response : responses.values()) {

            SmsTransactionDetail transDetail = smsTransactionDetailRepository
                    .findFirstByTransIdAndSentTo(tranId, response.getSendMessage().getTo())
                    .orElse(null);

            if (transDetail == null) {
                continue;
            }

            if (SmsApiResponse.isSmsSendSuccess(response.getErrorCode())) {

                int fee = feeService.getFee(tempDetails,
                        merchant.getCategoryId(),
                        response.getSendMessage().getTelco(),
                        SmsConstant.SMS_TYPE_CUSTOMER_CARE
                );

                transDetail.setStatus(SmsConstant.SENT_MESS_SUCCESS);
                transDetail.setResponseCode(SmsApiResponse.SUCCESS_CODE);
                transDetail.setCountMt(response.getMtCount());
                transDetail.setMsgLength(response.getMsgLength());
                transDetail.setTelco(response.getSendMessage().getTelco());
                transDetail.setSubReferenceId(response.getReferentId());
                transDetail.setFee(fee);
                transDetail.setTotalFee(fee * response.getMtCount());
            } else {
                transDetail.setStatus(SmsConstant.SENT_MESS_FAIL);
            }

            smsTransactionDetailRepository.save(transDetail);
        }
        return null;
    }

    private boolean hasAnySuccessResponse(Map<String, SmsApiResponse> responses) {
        return responses.values().stream()
                .anyMatch(response -> SmsApiResponse.isSmsSendSuccess(response.getErrorCode()));
    }

    private String getAnyErrorCode(Map<String, SmsApiResponse> responses) {
        return responses.values().stream()
                .findAny()
                .map(SmsApiResponse::getErrorCode)
                .orElse(SmsApiResponse.FAIL);
    }


}
