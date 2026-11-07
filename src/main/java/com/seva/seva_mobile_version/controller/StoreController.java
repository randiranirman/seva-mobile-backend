package com.seva.seva_mobile_version.controller;

import com.seva.seva_mobile_version.dto.request.CreateStoreRequest;
import com.seva.seva_mobile_version.dto.response.StoreResponse;
import com.seva.seva_mobile_version.service.StoreService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<StoreResponse> createStore(
            Authentication authentication, @Valid @RequestBody CreateStoreRequest request) {
        String ownerEmail = authentication.getName();
        log.info("/api/stores request received for ownerEmail={}", ownerEmail);

        StoreResponse response = storeService.createStore(ownerEmail, request);
        log.info("/api/stores succeeded for ownerEmail={} storeId={}", ownerEmail, response.id());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
