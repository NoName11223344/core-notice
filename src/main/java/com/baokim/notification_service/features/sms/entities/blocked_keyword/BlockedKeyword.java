package com.baokim.notification_service.features.sms.entities.blocked_keyword;

import com.baokim.notification_service.bases.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "blocked_keywords")
public class BlockedKeyword extends BaseEntity {
    @Column(nullable = false)
    private String keyword;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false)
    private Byte status;
}
