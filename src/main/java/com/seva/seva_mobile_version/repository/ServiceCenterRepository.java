package com.seva.seva_mobile_version.repository;

import com.seva.seva_mobile_version.entity.ServiceCenter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceCenterRepository extends JpaRepository<ServiceCenter, String> {

    boolean existsByOwnerId(String ownerId);

    boolean existsByBusinessRegistrationNumber(String businessRegistrationNumber);

    Optional<ServiceCenter> findByOwnerId(String ownerId);
}
