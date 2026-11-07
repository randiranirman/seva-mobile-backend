package com.seva.seva_mobile_version.repository;

import com.seva.seva_mobile_version.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {


    boolean existsByMobileNumber( String mobileNumber);

    Optional<User> findByEmail(String email);
    boolean existsByEmail( String email );

}
