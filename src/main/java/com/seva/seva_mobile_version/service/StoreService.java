package com.seva.seva_mobile_version.service;

import com.seva.seva_mobile_version.dto.request.CreateStoreRequest;
import com.seva.seva_mobile_version.dto.response.AuthResponse;
import com.seva.seva_mobile_version.dto.response.StoreCreationResponse;
import com.seva.seva_mobile_version.dto.response.StoreResponse;
import com.seva.seva_mobile_version.entity.ServiceCenter;
import com.seva.seva_mobile_version.entity.User;
import com.seva.seva_mobile_version.enums.Role;
import com.seva.seva_mobile_version.exception.StoreNotFoundException;
import com.seva.seva_mobile_version.repository.ServiceCenterRepository;
import com.seva.seva_mobile_version.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class StoreService {

    private final ServiceCenterRepository serviceCenterRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public StoreService(
            ServiceCenterRepository serviceCenterRepository,
            UserRepository userRepository,
            AuthService authService) {
        this.serviceCenterRepository = serviceCenterRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Transactional
    public StoreCreationResponse createStore(String ownerEmail, CreateStoreRequest request) {
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

        String normalizedBrNumber = request.businessRegistrationNumber().trim().toUpperCase();

        if (serviceCenterRepository.existsByBusinessRegistrationNumber(normalizedBrNumber)) {
            log.warn("Store creation rejected: businessRegistrationNumber={} already exists", normalizedBrNumber);
            throw new IllegalArgumentException("A store with this business registration number already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        ServiceCenter store = new ServiceCenter();
        store.setOwner(owner);
        store.setStoreName(request.storeName());
        store.setAddress(request.address());
        store.setArea(request.area());
        store.setOwnerName(request.ownerName());
        store.setOwnerContactNumber(request.ownerContactNumber());
        store.setBusinessRegistrationNumber(normalizedBrNumber);
        store.setLatitude(request.latitude());
        store.setLongitude(request.longitude());
        store.setCreatedAt(now);
        store.setUpdatedAt(now);

        serviceCenterRepository.save(store);
        log.info("Created store id={} for ownerId={}", store.getId(), owner.getId());

        AuthResponse tokens = authService.buildAuthResponse(owner);

        return new StoreCreationResponse(toResponse(store), tokens);
    }

    public StoreResponse getStoreByOwnerId(String ownerId, String requesterEmail) {
        log.debug("Fetching store for ownerId={} requesterEmail={}", ownerId, requesterEmail);

        ServiceCenter store = serviceCenterRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new StoreNotFoundException("No store found for ownerId: " + ownerId));

        if (!store.getOwner().getEmail().equals(requesterEmail)) {
            log.warn("Store lookup rejected: requesterEmail={} is not the owner of ownerId={}", requesterEmail, ownerId);
            throw new AccessDeniedException("You can only view your own store");
        }

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
                store.getBusinessRegistrationNumber(),
                store.getLatitude(),
                store.getLongitude(),
                store.getOwner().getId(),
                store.getCreatedAt(),
                store.getUpdatedAt());
    }
}
