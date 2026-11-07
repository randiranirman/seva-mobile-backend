package com.seva.seva_mobile_version.service;

import com.seva.seva_mobile_version.dto.request.CreateStoreRequest;
import com.seva.seva_mobile_version.dto.response.StoreResponse;
import com.seva.seva_mobile_version.entity.ServiceCenter;
import com.seva.seva_mobile_version.entity.User;
import com.seva.seva_mobile_version.enums.Role;
import com.seva.seva_mobile_version.repository.ServiceCenterRepository;
import com.seva.seva_mobile_version.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class StoreService {

    private final ServiceCenterRepository serviceCenterRepository;
    private final UserRepository userRepository;

    public StoreService(ServiceCenterRepository serviceCenterRepository, UserRepository userRepository) {
        this.serviceCenterRepository = serviceCenterRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public StoreResponse createStore(String ownerEmail, CreateStoreRequest request) {
        log.debug("Creating store for ownerEmail={}", ownerEmail);

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + ownerEmail));

        if (owner.getRole() != Role.STORE_OWNER) {
            log.warn("Store creation rejected: userId={} role={} is not a store owner", owner.getId(), owner.getRole());
            throw new IllegalArgumentException("Only store owners can create a store");
        }

        if (serviceCenterRepository.existsByOwnerId(owner.getId())) {
            log.warn("Store creation rejected: ownerId={} already owns a store", owner.getId());
            throw new IllegalArgumentException("You already own a store");
        }

        LocalDateTime now = LocalDateTime.now();

        ServiceCenter store = new ServiceCenter();
        store.setOwner(owner);
        store.setStoreName(request.storeName());
        store.setAddress(request.address());
        store.setArea(request.area());
        store.setOwnerName(request.ownerName());
        store.setOwnerContactNumber(request.ownerContactNumber());
        store.setCreatedAt(now);
        store.setUpdatedAt(now);

        serviceCenterRepository.save(store);
        log.info("Created store id={} for ownerId={}", store.getId(), owner.getId());

        return toResponse(store);
    }

    private StoreResponse toResponse(ServiceCenter store) {
        return new StoreResponse(
                store.getId(),
                store.getStoreName(),
                store.getAddress(),
                store.getArea(),
                store.getOwnerName(),
                store.getOwnerContactNumber(),
                store.getOwner().getId(),
                store.getCreatedAt(),
                store.getUpdatedAt());
    }
}
