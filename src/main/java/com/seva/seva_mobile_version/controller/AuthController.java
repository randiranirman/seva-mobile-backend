package com.seva.seva_mobile_version.controller;

import com.seva.seva_mobile_version.dto.request.LoginRequestOwner;
import com.seva.seva_mobile_version.dto.request.RefreshTokenRequest;
import com.seva.seva_mobile_version.dto.request.RegisterRequestOwner;
import com.seva.seva_mobile_version.dto.request.VerifyEmailRequest;
import com.seva.seva_mobile_version.dto.response.AuthResponse;
import com.seva.seva_mobile_version.dto.response.MessageResponse;
import com.seva.seva_mobile_version.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")

@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequestOwner request) {
        log.info("/register request received for email={}", request.email());
        MessageResponse response = authService.register(request);
        log.info("/register succeeded for email={}", request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequestOwner request) {
        log.info("/login request received for email={}", request.email());
        AuthResponse response = authService.login(request);
        log.info("/login succeeded for email={}", request.email());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("/refresh request received");
        AuthResponse response = authService.refresh(request);
        log.info("/refresh succeeded");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<MessageResponse> verify(@Valid @RequestBody VerifyEmailRequest request) {
        log.info("/verify request received for email={}", request.email());
        MessageResponse response = authService.verifyEmail(request);
        log.info("/verify succeeded for email={}", request.email());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody RefreshTokenRequest request) {
        log.info("/logout request received");
        String accessToken = (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring("Bearer ".length())
                : null;
        MessageResponse response = authService.logout(accessToken, request.refreshToken());
        log.info("/logout succeeded");
        return ResponseEntity.ok(response);
    }
}
