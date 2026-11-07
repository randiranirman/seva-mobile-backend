package com.seva.seva_mobile_version.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType) {
}
