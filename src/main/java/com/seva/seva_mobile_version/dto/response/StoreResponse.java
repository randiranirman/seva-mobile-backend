package com.seva.seva_mobile_version.dto.response;

import java.time.LocalDateTime;

public record StoreResponse(
        String id,
        String storeName,
        String address,
        String area,
        String ownerName,
        String ownerContactNumber,
        String businessRegistrationNumber,
        Double latitude,
        Double longitude,
        String ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
