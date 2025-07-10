package com.baokim.notification_service.features.email.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailAttachmentInfo {
    private String fileName;
    private String filePath;
}
