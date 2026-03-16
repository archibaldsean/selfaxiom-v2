package com.selfaxiom.api.auth;

import com.selfaxiom.api.user.UserResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class AuthModels {

  private AuthModels() {
  }

  public record AuthenticatedUser(Long id, String username, String email) {
  }

  public static class LoginRequest {

    @NotBlank
    private String identifier;

    @NotBlank
    private String password;

    public String getIdentifier() {
      return identifier;
    }

    public void setIdentifier(String identifier) {
      this.identifier = identifier;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }

  public static class RegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }

  public static class AuthResponse {

    private final UserResponse user;
    private final String accessToken;

    public AuthResponse(UserResponse user, String accessToken) {
      this.user = user;
      this.accessToken = accessToken;
    }

    public UserResponse getUser() {
      return user;
    }

    public String getAccessToken() {
      return accessToken;
    }
  }

  public record AuthSessionIssue(AuthResponse response, String refreshToken) {
  }

  public record RefreshToken(String token, String tokenId, Instant expiresAt) {
  }

  public record ParsedRefreshToken(Long userId, String tokenId, Instant expiresAt) {
  }
}
