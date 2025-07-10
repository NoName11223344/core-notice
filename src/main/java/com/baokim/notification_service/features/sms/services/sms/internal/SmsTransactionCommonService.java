package com.baokim.notification_service.features.sms.services.sms.internal;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsTransactionCommonService {
    private final SmsTransactionRepository smsTransactionRepository;
    private final SmsTransactionDetailRepository smsTransactionDetailRepository;
    private final SmsApiResponseHandler smsApiResponseHandler;
    private final MerchantRepository merchantRepository;
    private final TempFeeDetailRepository tempFeeDetailRepository;
    private final FeeService feeService;

    public SmsTransaction updateSmsTransaction(String responseProvider, String tranId) {

        SmsApiResponse smsApiResponse = smsApiResponseHandler.handleSingleSmsResponse(responseProvider);

        SmsTransaction transaction = smsTransactionRepository.findByTransId(tranId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOTFOUND));

        boolean isSuccess = SmsApiResponse.isSmsSendSuccess(smsApiResponse.getErrorCode());

        transaction.setStatus(isSuccess ? SmsConstant.SENT_MESS_SUCCESS : SmsConstant.SENT_MESS_FAIL);
        transaction.setResponseCode(smsApiResponse.getErrorCode());
        transaction.setReferenceIdProvider(smsApiResponse.getReferentId());

        return smsTransactionRepository.save(transaction);
    }

    public SmsTransactionDetail updateSmsTransactionDetail(BaseSmsSendRequest request, String responseProvider, String tranId) {

        SmsApiResponse smsApiResponse = smsApiResponseHandler.handleSingleSmsResponse(responseProvider);

        Merchant merchant = merchantRepository.findByMerchantCode(request.getMerchantCode());

        List<TemFeeDetail> tempDetails = tempFeeDetailRepository.findAllByTempCodeAndStatus(
                merchant.getTempFeeCode(),
                TempFeeConstant.TEMP_ACTIVE
        );

        int fee = feeService.getFee(
                tempDetails, merchant.getCategoryId(),
                smsApiResponse.getSendMessage().getTelco(),
                SmsConstant.SMS_TYPE_CUSTOMER_CARE
        );

        SmsTransactionDetail smsTransactionDetail = smsTransactionDetailRepository.findByTransId(tranId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOTFOUND));

        if (SmsApiResponse.isSmsSendSuccess(smsApiResponse.getErrorCode())) {
            smsTransactionDetail.setCountMt(smsApiResponse.getMtCount());
            smsTransactionDetail.setMsgLength(smsApiResponse.getMsgLength());
            smsTransactionDetail.setTelco(smsApiResponse.getSendMessage().getTelco());
            smsTransactionDetail.setSubReferenceId(smsApiResponse.getReferentId());
            smsTransactionDetail.setTotalFee(fee * smsApiResponse.getMtCount());
            smsTransactionDetail.setStatus(SmsConstant.SENT_MESS_SUCCESS);
            smsTransactionDetail.setFee(fee);
        } else {
            smsTransactionDetail.setStatus(SmsConstant.SENT_MESS_FAIL);
        }

        return smsTransactionDetailRepository.save(smsTransactionDetail);
    }
}
