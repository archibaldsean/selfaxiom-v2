package com.selfaxiom.api.auth;

import com.selfaxiom.api.auth.AuthExceptions.InvalidCredentialsException;
import com.selfaxiom.api.user.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RefreshSessionService {

  private final RefreshSessionRepository refreshSessionRepository;

  public RefreshSessionService(RefreshSessionRepository refreshSessionRepository) {
    this.refreshSessionRepository = refreshSessionRepository;
  }

  public void create(User user, String tokenId, Instant expiresAt) {
    RefreshSession session = new RefreshSession(UUID.randomUUID(), user, hashTokenId(tokenId), expiresAt, null);
    refreshSessionRepository.save(session);
  }

  public void assertValid(Long userId, String tokenId) {
    refreshSessionRepository.findByUser_IdAndTokenHashAndRevokedAtIsNullAndExpiresAtAfter(
        userId,
        hashTokenId(tokenId),
        Instant.now()).orElseThrow(() -> new InvalidCredentialsException("Invalid refresh session"));
  }

  public void revokeIfExists(Long userId, String tokenId) {
    refreshSessionRepository.findByUser_IdAndTokenHashAndRevokedAtIsNullAndExpiresAtAfter(
        userId,
        hashTokenId(tokenId),
        Instant.now()).ifPresent(session -> {
          session.setRevokedAt(Instant.now());
          refreshSessionRepository.save(session);
        });
  }

  public void rotate(Long userId, String currentTokenId, User user, String newTokenId, Instant newExpiresAt) {
    revokeIfExists(userId, currentTokenId);
    create(user, newTokenId, newExpiresAt);
  }

  private String hashTokenId(String tokenId) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashed = digest.digest(tokenId.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hashed);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("Unable to hash token id", e);
    }
  }
}
