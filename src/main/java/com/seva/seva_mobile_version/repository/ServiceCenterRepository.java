package com.seva.seva_mobile_version.repository;

import com.seva.seva_mobile_version.entity.ServiceCenter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCenterRepository extends JpaRepository<ServiceCenter, String> {

    boolean existsByOwnerId(String ownerId);
}
