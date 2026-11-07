package com.seva.seva_mobile_version.repository;

import com.seva.seva_mobile_version.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {

    boolean existsByJti(String jti);
}
