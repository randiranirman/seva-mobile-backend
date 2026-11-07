package com.seva.seva_mobile_version.service;

import com.seva.seva_mobile_version.dto.request.LoginRequestOwner;
import com.seva.seva_mobile_version.dto.request.RefreshTokenRequest;
import com.seva.seva_mobile_version.dto.request.RegisterRequestOwner;
import com.seva.seva_mobile_version.dto.request.VerifyEmailRequest;
import com.seva.seva_mobile_version.dto.response.AuthResponse;
import com.seva.seva_mobile_version.dto.response.MessageResponse;
import com.seva.seva_mobile_version.entity.RevokedToken;
import com.seva.seva_mobile_version.entity.User;
import com.seva.seva_mobile_version.enums.Role;
import com.seva.seva_mobile_version.exception.EmailNotVerifiedException;
import com.seva.seva_mobile_version.repository.RevokedTokenRepository;
import com.seva.seva_mobile_version.repository.UserRepository;
import com.seva.seva_mobile_version.security.JwtService;
import com.seva.seva_mobile_version.utils.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final long verificationCodeExpirationMs;

    public AuthService(
            UserRepository userRepository,
            RevokedTokenRepository revokedTokenRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmailService emailService,
            @Value("${verification.code-expiration-ms}") long verificationCodeExpirationMs) {
        this.userRepository = userRepository;
        this.revokedTokenRepository = revokedTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificationCodeExpirationMs = verificationCodeExpirationMs;
    }

    @Transactional
    public MessageResponse register(RegisterRequestOwner request) {
        log.debug("Registering owner with email={}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            log.warn("Registration rejected: email={} already exists", request.email());
            throw new IllegalArgumentException("A user with this email already exists");
        }

         if( userRepository.existsByMobileNumber(request.mobileNumber())) {
             log.warn("Registration rejected: mobileNumber already exists for email={}", request.email());
             throw new IllegalArgumentException(" A user with this mobile number exists");
         }

        LocalDateTime now = LocalDateTime.now();

        String code = generateVerificationCode();

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setMobileNumber(request.mobileNumber());
        user.setRole(Role.STORE_OWNER);
        user.setEmailVerified(false);
        user.setVerificationCode(code);
        user.setVerificationCodeExpiresAt(now.plusNanos(verificationCodeExpirationMs * 1_000_000));
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userRepository.save(user);
        log.info("Registered new {} user id={} email={}", user.getRole(), user.getId(), user.getEmail());

        emailService.sendVerificationEmail(user.getEmail(), code);

        return new MessageResponse("Registration successful. Please check your email to verify your account.");
    }

    public MessageResponse verifyEmail(VerifyEmailRequest request) {
        log.debug("Verification attempt for email={}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Verification failed: no user found for email={}", request.email());
                    return new IllegalArgumentException("Invalid verification code");
                });

        if (user.isEmailVerified()) {
            log.debug("Verification skipped: email={} already verified", request.email());
            return new MessageResponse("Email already verified.");
        }

        if (user.getVerificationCode() == null
                || !user.getVerificationCode().equals(request.code())
                || user.getVerificationCodeExpiresAt() == null
                || user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Verification failed: invalid or expired code for email={}", request.email());
            throw new IllegalArgumentException("Invalid or expired verification code");
        }

        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Email verified for userId={} email={}", user.getId(), user.getEmail());
        return new MessageResponse("Email verified successfully. You can now log in.");
    }

    private String generateVerificationCode() {
        int code = RANDOM.nextInt(1_000_000);
        return String.format("%06d", code);
    }

    public AuthResponse login(LoginRequestOwner request) {
        log.debug("Login attempt for email={}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login failed: no user found for email={}", request.email());
                    return new BadCredentialsException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed: bad password for email={}", request.email());
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!user.isEmailVerified()) {
            log.warn("Login blocked: email={} not verified", request.email());
            throw new EmailNotVerifiedException("Please verify your email before logging in");
        }

        log.info("Login succeeded for userId={} email={}", user.getId(), user.getEmail());
        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        log.debug("Refresh token requested");

        if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            log.warn("Refresh rejected: invalid or expired refresh token");
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        if (revokedTokenRepository.existsByJti(jwtService.extractJti(refreshToken))) {
            log.warn("Refresh rejected: refresh token has been revoked");
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        String email = jwtService.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Refresh rejected: no user found for email={}", email);
                    return new BadCredentialsException("Invalid or expired refresh token");
                });

        log.info("Issued new tokens via refresh for userId={} email={}", user.getId(), user.getEmail());
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(accessToken, refreshToken, "Bearer");
    }

    @Transactional
    public MessageResponse logout(String accessToken, String refreshToken) {
        log.debug("Logout requested");

        revokeTokenIfPresent(accessToken);
        revokeTokenIfPresent(refreshToken);

        log.info("Logout processed, tokens revoked where valid");
        return new MessageResponse("Logged out successfully.");
    }

    private void revokeTokenIfPresent(String token) {
        if (token == null || token.isBlank()) {
            return;
        }

        try {
            String jti = jwtService.extractJti(token);

            if (revokedTokenRepository.existsByJti(jti)) {
                return;
            }

            RevokedToken revokedToken = new RevokedToken();
            revokedToken.setJti(jti);
            revokedToken.setExpiresAt(jwtService.extractExpirationAsLocalDateTime(token));
            revokedToken.setRevokedAt(LocalDateTime.now());
            revokedTokenRepository.save(revokedToken);
        } catch (Exception ex) {
            log.warn("Could not revoke token during logout: {}", ex.getMessage());
        }
    }
}
