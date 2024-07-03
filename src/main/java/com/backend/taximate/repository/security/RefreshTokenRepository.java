package com.backend.taximate.repository.security;

import com.backend.taximate.domain.security.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
