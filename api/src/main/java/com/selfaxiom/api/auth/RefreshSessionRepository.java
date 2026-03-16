package com.selfaxiom.api.auth;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, UUID> {

  Optional<RefreshSession> findByUser_IdAndTokenHashAndRevokedAtIsNullAndExpiresAtAfter(Long userId,
      String tokenHash,
      Instant now);
}
