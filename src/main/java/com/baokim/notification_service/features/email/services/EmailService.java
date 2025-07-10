package com.baokim.notification_service.features.email.services;

import com.baokim.notification_service.bases.responses.ApiResponse;
import com.baokim.notification_service.commons.constants.ResponseConstants;
import com.baokim.notification_service.commons.exceptions.AppException;
import com.baokim.notification_service.commons.exceptions.ErrorCode;
import com.baokim.notification_service.commons.utils.FileUtils;
import com.baokim.notification_service.commons.utils.HelperUtil;
import com.baokim.notification_service.features.email.configs.EmailConfig;
import com.baokim.notification_service.features.email.constants.EmailConstant;
import com.baokim.notification_service.features.email.dtos.requests.EmailAttachmentInfo;
import com.baokim.notification_service.features.email.dtos.requests.EmailRequest;
import com.baokim.notification_service.features.email.entities.EmailRecipient;
import com.baokim.notification_service.features.email.entities.EmailTransaction;
import com.baokim.notification_service.features.email.kafka.producer.EmailKafkaProducer;
import com.baokim.notification_service.features.email.repositories.EmailRecipientRepository;
import com.baokim.notification_service.features.email.repositories.EmailTransactionRepository;
import com.baokim.notification_service.features.sms.services.request_log.RequestLogService;
import com.baokim.notification_service.integrations.googlechat.GoogleChatNotifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final EmailConfig emailConfig;
    private final ObjectMapper objectMapper;
    private final EmailTransactionRepository emailTransactionRepository;
    private final RequestLogService requestLogService;
    private final EmailKafkaProducer emailKafkaProducer;
    private final FileUtils fileUtils;
    private final EmailRecipientRepository emailRecipientRepository;
    private final GoogleChatNotifier googleChatNotifier;

    @Value("${email.times_retry}")
    private int timesRetry;

    @Transactional
    public ApiResponse<String> sendEmail(EmailRequest emailRequest) {

        ApiResponse<String> apiResponse = new ApiResponse<>();

        if (HelperUtil.hasDuplicateEmails(emailRequest.getTo(), emailRequest.getCc())) {
            throw new AppException(ErrorCode.DUPLICATE_EMAIL);
        }
        try {
            Long emailId = saveEmailTransaction(emailRequest);

            saveEmailRecipient(emailId, emailRequest.getTo(), emailRequest.getCc());

            emailKafkaProducer.sendToKafka(emailId, emailRequest.getRequestId());

        }catch (Exception e) {
            log.error("Lỗi gửi email: " + e.getMessage(), e);
            apiResponse.setCode(ResponseConstants.ERROR_CODE);
            apiResponse.setMessage("Lỗi gửi email: " + e.getMessage());
        }

        requestLogService.saveRequestLogEmail(emailRequest, apiResponse);

        return apiResponse;
    }


    public Long saveEmailTransaction(EmailRequest emailRequest) throws Exception {

        EmailConfig.ProviderConfig provider = emailConfig.getProviderConfig(emailConfig.getProvider());

        String fromEmail = emailRequest.getFromEmail() != null ? emailRequest.getFromEmail() : provider.getFromEmail();

        EmailTransaction email = new EmailTransaction();
        email.setStatus(EmailConstant.PENDING);
        email.setRequestId(emailRequest.getRequestId());
        email.setContentType(emailRequest.getContentType());
        email.setEmailFromName(emailRequest.getFromName());
        email.setEmailFrom(fromEmail);
        email.setRetryCount(EmailConstant.DEFAULT_RETRY);
        email.setSubject(emailRequest.getSubject());
        email.setContent(emailRequest.getContent());
        email.setProvider(emailConfig.getProvider());
        email.setMerchantCode(emailRequest.getMerchantCode());

        List<EmailAttachmentInfo> attachmentInfos = new ArrayList<>();

        if (emailRequest.getFiles() != null && !emailRequest.getFiles().isEmpty()) {
            for (MultipartFile multipartFile : emailRequest.getFiles()) {
                if (multipartFile == null || multipartFile.isEmpty()) {
                    continue; // Bỏ qua file null hoặc không có nội dung
                }

                String filePath = this.fileUtils.uploadFile(multipartFile);
                String fileName = multipartFile.getOriginalFilename();

                EmailAttachmentInfo info = new EmailAttachmentInfo();
                info.setFileName(fileName);
                info.setFilePath(filePath);
                attachmentInfos.add(info);
            }

            if (!attachmentInfos.isEmpty()) {
                String attachmentsJson = objectMapper.writeValueAsString(attachmentInfos);
                email.setAttachments(attachmentsJson);
            }
        }


        EmailTransaction saved = emailTransactionRepository.save(email);

        return saved.getId();
    }

    public void saveEmailRecipient(Long emailId, String emailTo, String emailCc) {
        List<EmailRecipient> recipientsList = new ArrayList<>();

        String[] toEmails = emailTo.split(",");
        for (String email : toEmails) {
            EmailRecipient recipient = getEmailRecipientDetail(emailId, email.trim(), EmailConstant.EMAIL_TO);
            recipientsList.add(recipient);
        }

        if (emailCc != null && !emailCc.isEmpty()) {
            String[] ccEmails = emailCc.split(",");
            for (String email : ccEmails) {
                EmailRecipient recipient = getEmailRecipientDetail(emailId, email.trim(), EmailConstant.EMAIL_CC);
                recipientsList.add(recipient);
            }
        }
        emailRecipientRepository.saveAll(recipientsList);
    }


    private EmailRecipient getEmailRecipientDetail(Long emailTranId, String email, Byte emailType) {
        EmailRecipient recipient = new EmailRecipient();
        recipient.setEmailId(emailTranId);
        recipient.setEmailType(emailType);
        recipient.setEmailAddress(email);

        return recipient;
    }

    public void retrySentEmail(EmailTransaction emailTransaction) {

        if (emailTransaction.getRetryCount() >= timesRetry) {
            googleChatNotifier.sendMessage(emailTransaction.getRequestId() + " Email id = " + emailTransaction.getId()  + " quá số lần gửi lại");
            return;
        }
        emailTransaction.setRetryCount((byte) (emailTransaction.getRetryCount() + 1));
        emailTransaction.setStatus(EmailConstant.PENDING);
        emailTransactionRepository.save(emailTransaction);
        emailKafkaProducer.sendToKafka(emailTransaction.getId(), emailTransaction.getRequestId());
    }
}
