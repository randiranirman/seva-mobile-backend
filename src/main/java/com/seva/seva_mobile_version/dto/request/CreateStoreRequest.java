package com.seva.seva_mobile_version.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateStoreRequest(
        @NotBlank String storeName,
        @NotBlank String address,
        @NotBlank String area,
        @NotBlank String ownerName,
        @NotBlank @Size(min = 10, max = 10) String ownerContactNumber) {
}
