package com.baokim.notification_service.features.sms.repositories.brandname;
import com.baokim.notification_service.features.sms.entities.brandname.BrandName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandNameRepository extends JpaRepository<BrandName, String> {
    boolean existsByBrandNameAndStatus(String brandName, Byte status);
}
