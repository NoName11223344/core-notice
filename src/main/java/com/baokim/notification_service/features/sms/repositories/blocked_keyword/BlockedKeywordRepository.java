package com.baokim.notification_service.features.sms.repositories.blocked_keyword;

import com.baokim.notification_service.features.sms.entities.blocked_keyword.BlockedKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockedKeywordRepository extends JpaRepository<BlockedKeyword, String> {

    List<BlockedKeyword> findByStatus(Byte status);
}
