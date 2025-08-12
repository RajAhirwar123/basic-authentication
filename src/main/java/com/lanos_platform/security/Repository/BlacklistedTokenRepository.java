package com.lanos_platform.security.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lanos_platform.security.Modal.BlacklistedToken;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, String> {
    boolean existsByToken(String token);
}
