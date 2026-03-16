package com.selfaxiom.api.auth;

import com.selfaxiom.api.auth.AuthExceptions.InvalidCredentialsException;
import com.selfaxiom.api.auth.AuthModels.AuthenticatedUser;
import com.selfaxiom.api.auth.AuthModels.ParsedRefreshToken;
import com.selfaxiom.api.auth.AuthModels.RefreshToken;
import com.selfaxiom.api.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private static final String TOKEN_TYPE_CLAIM = "typ";
  private static final String TOKEN_TYPE_ACCESS = "access";
  private static final String TOKEN_TYPE_REFRESH = "refresh";

  private final AuthProperties authProperties;

  public JwtService(AuthProperties authProperties) {
    this.authProperties = authProperties;
  }

  public String generateAccessToken(User user) {
    Instant now = Instant.now();
    Instant expiresAt = now.plusSeconds(authProperties.getJwt().getAccessTtlSeconds());

    return Jwts.builder()
        .subject(user.getId().toString())
        .claim("username", user.getUsername())
        .claim("email", user.getEmail())
        .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACCESS)
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .signWith(accessKey())
        .compact();
  }

  public RefreshToken generateRefreshToken(User user) {
    Instant now = Instant.now();
    Instant expiresAt = now.plusSeconds(authProperties.getJwt().getRefreshTtlSeconds());
    String tokenId = UUID.randomUUID().toString();

    String token = Jwts.builder()
        .subject(user.getId().toString())
        .id(tokenId)
        .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_REFRESH)
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .signWith(refreshKey())
        .compact();

    return new RefreshToken(token, tokenId, expiresAt);
  }

  public AuthenticatedUser parseAccessToken(String token) {
    Claims claims = parseClaims(token, accessKey());
    ensureTokenType(claims, TOKEN_TYPE_ACCESS);

    Long userId = Long.parseLong(claims.getSubject());
    String username = claims.get("username", String.class);
    String email = claims.get("email", String.class);
    return new AuthenticatedUser(userId, username, email);
  }

  public ParsedRefreshToken parseRefreshToken(String token) {
    Claims claims = parseClaims(token, refreshKey());
    ensureTokenType(claims, TOKEN_TYPE_REFRESH);

    Long userId = Long.parseLong(claims.getSubject());
    String tokenId = claims.getId();
    Instant expiresAt = claims.getExpiration().toInstant();
    return new ParsedRefreshToken(userId, tokenId, expiresAt);
  }

  private Claims parseClaims(String token, SecretKey key) {
    try {
      return Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (JwtException | IllegalArgumentException e) {
      throw new InvalidCredentialsException("Invalid token");
    }
  }

  private void ensureTokenType(Claims claims, String expectedType) {
    String actualType = claims.get(TOKEN_TYPE_CLAIM, String.class);
    if (!expectedType.equals(actualType)) {
      throw new InvalidCredentialsException("Invalid token");
    }
  }

  private SecretKey accessKey() {
    return Keys.hmacShaKeyFor(authProperties.getJwt().getAccessSecret().getBytes(StandardCharsets.UTF_8));
  }

  private SecretKey refreshKey() {
    return Keys.hmacShaKeyFor(authProperties.getJwt().getRefreshSecret().getBytes(StandardCharsets.UTF_8));
  }
}
