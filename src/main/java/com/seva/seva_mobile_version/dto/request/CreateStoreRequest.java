package com.seva.seva_mobile_version.dto.request;

import com.seva.seva_mobile_version.validation.ValidBrNumber;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStoreRequest(
        @NotBlank String storeName,
        @NotBlank String address,
        @NotBlank String area,
        @NotBlank String ownerName,
        @NotBlank @Size(min = 10, max = 10) String ownerContactNumber,
        @NotBlank @ValidBrNumber String businessRegistrationNumber,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude) {
}
