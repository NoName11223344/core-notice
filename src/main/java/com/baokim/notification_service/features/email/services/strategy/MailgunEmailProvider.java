package com.baokim.notification_service.features.email.services.strategy;

import com.baokim.notification_service.commons.utils.FileUtils;
import com.baokim.notification_service.features.email.configs.EmailConfig;
import com.baokim.notification_service.features.email.constants.EmailConstant;
import com.baokim.notification_service.features.email.dtos.requests.EmailAttachmentInfo;
import com.baokim.notification_service.features.email.entities.EmailRecipient;
import com.baokim.notification_service.features.email.entities.EmailTransaction;
import com.baokim.notification_service.features.email.repositories.EmailRecipientRepository;
import com.baokim.notification_service.features.email.repositories.EmailTransactionRepository;
import com.baokim.notification_service.features.email.services.EmailService;
import com.baokim.notification_service.integrations.googlechat.GoogleChatNotifier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component("mailgun")
@RequiredArgsConstructor
public class MailgunEmailProvider implements EmailProvider {

    private final OkHttpClient httpClient;
    private final EmailConfig emailConfig;
    private final EmailTransactionRepository emailTransactionRepository;
    private final ObjectMapper objectMapper;
    private final GoogleChatNotifier googleChatNotifier;
    private final EmailRecipientRepository emailRecipientRepository;
    private final EmailService emailService;

    @Override
    @Async
    public void sendEmail(Long emailId) {
        EmailTransaction email = getEmailByIdAndStatus(emailId);
        if (email == null) {
            return;
        }

        try {
            List<EmailRecipient> emailRecipients = emailRecipientRepository.getEmailsRecipientByEmailId(emailId);
            MultipartBody.Builder bodyBuilder = createEmailBody(email, emailRecipients);
            List<String> pathFiles = handleAttachments(email, bodyBuilder);

            sendEmailRequest(email, bodyBuilder, pathFiles);
        } catch (Exception e) {
            email.setStatus(EmailConstant.FAILED);
            googleChatNotifier.sendMessage("[Mailgun] Gửi email bị lỗi: id = " + emailId + " message: " + e.getMessage());
        }
        email.setSentAt(LocalDateTime.now());
        emailTransactionRepository.save(email);

        if (email.getStatus().equals(EmailConstant.FAILED)) {
            emailService.retrySentEmail(email);
        }
    }

    private EmailTransaction getEmailByIdAndStatus(Long emailId) {
        EmailTransaction email = emailTransactionRepository.findByIdAndStatus(emailId, EmailConstant.PENDING)
                .orElse(null);
        if (email == null) {
            googleChatNotifier.sendMessage("[Mailgun] Gửi email id k tìm thấy : id = " + emailId);
            log.info("Mailgun Không tìm thấy id hoặc id không hợp le {}", emailId);
        }
        return email;
    }

    private MultipartBody.Builder createEmailBody(EmailTransaction email, List<EmailRecipient> emailRecipients) {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("from", String.format("%s <%s>", email.getEmailFromName(), email.getEmailFrom()))
                .addFormDataPart("subject", email.getSubject());

        if (EmailConstant.HTML.equals(email.getContentType())) {
            bodyBuilder.addFormDataPart("html", email.getContent());
        } else {
            bodyBuilder.addFormDataPart("text", email.getContent());
        }

        List<String> emailsTo = getEmailsByType(emailRecipients, EmailConstant.EMAIL_TO);
        for (String emailTo : emailsTo) {
            bodyBuilder.addFormDataPart("to", emailTo);
        }

        List<String> ccEmails = getEmailsByType(emailRecipients, EmailConstant.EMAIL_CC);
        if (!ccEmails.isEmpty()) {
            for (String ccEmail : ccEmails) {
                bodyBuilder.addFormDataPart("cc", ccEmail);
            }
        }
        return bodyBuilder;
    }

    private List<String> handleAttachments(EmailTransaction email, MultipartBody.Builder bodyBuilder) throws Exception {
        List<String> pathFiles = new ArrayList<>();
        String attachmentsJson = email.getAttachments();
        if (attachmentsJson != null && !attachmentsJson.isEmpty()) {
            List<EmailAttachmentInfo> attachmentInfos = objectMapper.readValue(
                    attachmentsJson, new TypeReference<List<EmailAttachmentInfo>>(){}
            );
            for (EmailAttachmentInfo multipartFile : attachmentInfos) {
                String fileName = multipartFile.getFileName();
                pathFiles.add(multipartFile.getFilePath());
                byte[] fileBytes = FileUtils.readFile(multipartFile.getFilePath());
                File tempFile = FileUtils.writeTempFile(fileBytes, fileName);
                bodyBuilder.addFormDataPart(
                        "attachment",
                        fileName,
                        RequestBody.create(tempFile, MediaType.parse("application/octet-stream"))
                );
            }
        }
        return pathFiles;
    }

    private void sendEmailRequest(EmailTransaction email, MultipartBody.Builder bodyBuilder, List<String> pathFiles) throws IOException {
        EmailConfig.ProviderConfig provider = emailConfig.getProviderConfig(EmailConstant.PROVIDER_MAILGUN);
        Request request = new Request.Builder()
                .url(provider.getDomain())
                .header("Authorization", Credentials.basic("api", provider.getApiKey()))
                .post(bodyBuilder.build())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                email.setStatus(EmailConstant.SENT);
                FileUtils.deleteFiles(pathFiles);
            } else {
                email.setStatus(EmailConstant.FAILED);
                String message = String.format(
                        "Gửi email request id = %s mailgun thất bại: %s",
                        email.getRequestId(),
                        response.body() != null ? response.body().string() : "None"
                );
                log.info(message);
                googleChatNotifier.sendMessage(message);
            }
        }
    }

    public List<String> getEmailsByType(List<EmailRecipient> emailRecipients, byte emailType) {
        return emailRecipients.stream()
                .filter(er -> er.getEmailType() == emailType)
                .map(EmailRecipient::getEmailAddress)
                .collect(Collectors.toList());
    }

}
