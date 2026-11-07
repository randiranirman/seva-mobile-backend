package com.seva.seva_mobile_version.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestOwner(
        @Email @NotBlank String email,
        @NotBlank String password,
        @Size(min = 10, max = 10) String mobileNumber) {
}
