package com.baokim.notification_service.features.email.services.strategy;

import com.baokim.notification_service.commons.constants.ResponseConstants;
import com.baokim.notification_service.commons.utils.DateTimeUtil;
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
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component("sendgrid")
@Slf4j
@RequiredArgsConstructor
public class SendGridEmailProvider implements EmailProvider {

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
            Mail mail = createEmailMail(email, emailRecipients);
            List<String> pathFiles = handleAttachments(email, mail);

            sendEmailRequest(mail, email, pathFiles);
        } catch (Exception e) {
            email.setStatus(EmailConstant.FAILED);
            log.error("[SendGrid] Gửi email bị lỗi emailId {}", emailId, e);
            googleChatNotifier.sendMessage("[SendGrid] Gửi email bị lỗi: id = " + emailId + " message: " + e.getMessage());
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
            googleChatNotifier.sendMessage("[SendGrid] Gửi email thấy id hoặc k hợp lệ: id = " + emailId);
            log.info("Không tìm thấy id hoặc id không hợp le {}", emailId);
        }
        return email;
    }

    private Mail createEmailMail(EmailTransaction email, List<EmailRecipient> emailRecipients) {
        Email fromAddr = new Email(email.getEmailFrom(), email.getEmailFromName());

        Content emailContent = (EmailConstant.HTML.equals(email.getContentType()))
                ? new Content("text/html", email.getContent())
                : new Content("text/plain", email.getContent());

        Mail mail = new Mail();
        mail.setFrom(fromAddr);
        mail.addContent(emailContent);

        Personalization personalization = new Personalization();
        addRecipientsToPersonalization(personalization, emailRecipients);
        personalization.setSubject(email.getSubject());
        mail.addPersonalization(personalization);

        return mail;
    }

    private void addRecipientsToPersonalization(Personalization personalization, List<EmailRecipient> emailRecipients) {
        List<String> toEmails = getEmailsByType(emailRecipients, EmailConstant.EMAIL_TO);
        toEmails.forEach(toEmail -> personalization.addTo(new Email(toEmail)));

        List<String> ccEmails = getEmailsByType(emailRecipients, EmailConstant.EMAIL_CC);
        ccEmails.forEach(ccEmail -> personalization.addCc(new Email(ccEmail)));
    }

    private List<String> handleAttachments(EmailTransaction email, Mail mail) throws Exception {
        List<String> pathFiles = new ArrayList<>();
        String attachmentsJson = email.getAttachments();
        if (attachmentsJson != null && !attachmentsJson.isEmpty()) {
            List<EmailAttachmentInfo> attachmentInfos = objectMapper.readValue(attachmentsJson, new TypeReference<List<EmailAttachmentInfo>>(){});
            for (EmailAttachmentInfo info : attachmentInfos) {
                String filePath = info.getFilePath();
                pathFiles.add(filePath);
                byte[] readBytes = FileUtils.readFile(filePath);
                Attachments attachment = new Attachments();
                attachment.setContent(Base64.getEncoder().encodeToString(readBytes));
                attachment.setType("application/octet-stream");
                attachment.setFilename(info.getFileName());
                attachment.setDisposition("attachment");
                mail.addAttachments(attachment);
            }
        }
        return pathFiles;
    }

    private void sendEmailRequest(Mail mail, EmailTransaction email, List<String> pathFiles) throws IOException {
        EmailConfig.ProviderConfig provider = emailConfig.getProviderConfig(EmailConstant.PROVIDER_SENDGRID);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        SendGrid sendGrid = new SendGrid(provider.getApiKey());
        Response response = sendGrid.api(request);

        if (response.getStatusCode() >= ResponseConstants.SUCCESS_CODE && response.getStatusCode() < ResponseConstants.HTTP_OK_MAX) {
            email.setStatus(EmailConstant.SENT);
            FileUtils.deleteFiles(pathFiles);
        } else {
            String message = String.format(
                    "Gửi email request id = %s sendgrid thất bại: %s",
                    email.getRequestId(),
                    response.getBody() != null ? response.getBody() : "None"
            );
            log.info(message);
            googleChatNotifier.sendMessage(message);
            email.setStatus(EmailConstant.FAILED);
        }
    }
    public List<String> getEmailsByType(List<EmailRecipient> emailRecipients, byte emailType) {
        return emailRecipients.stream()
                .filter(er -> er.getEmailType() == emailType)
                .map(EmailRecipient::getEmailAddress)
                .collect(Collectors.toList());
    }
}

