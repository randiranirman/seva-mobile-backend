package com.seva.seva_mobile_version.service;

import com.seva.seva_mobile_version.entity.User;
import com.seva.seva_mobile_version.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Email and username are stored BCrypt-hashed, so they can't be queried by SQL equality
 * (each encode call produces a different salt/hash for the same input). Lookups here fall
 * back to a full-table scan with {@link BCryptPasswordEncoder#matches}, which is O(n) per call.
 */
@Slf4j
@Service
public class UserLookupService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserLookupService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByEmail(String rawEmail) {
        return userRepository.findAll().stream()
                .filter(user -> passwordEncoder.matches(rawEmail, user.getEmail()))
                .findFirst();
    }

    public boolean existsByEmail(String rawEmail) {
        return findByEmail(rawEmail).isPresent();
    }
}
