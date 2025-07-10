package com.baokim.notification_service.features.sms.repositories.merchant;

import com.baokim.notification_service.features.sms.entities.merchant.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, String> {
    Merchant findByMerchantCode(String merchantCode);

    boolean existsByMerchantCode(String merchantCode);
}
