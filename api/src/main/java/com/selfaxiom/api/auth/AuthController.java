package com.selfaxiom.api.auth;

import jakarta.validation.Valid;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import com.selfaxiom.api.auth.AuthModels.AuthResponse;
import com.selfaxiom.api.auth.AuthModels.AuthSessionIssue;
import com.selfaxiom.api.auth.AuthModels.AuthenticatedUser;
import com.selfaxiom.api.auth.AuthModels.LoginRequest;
import com.selfaxiom.api.auth.AuthModels.RegisterRequest;
import com.selfaxiom.api.user.UserResponse;
import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;
  private final AuthProperties authProperties;

  public AuthController(AuthService authService, AuthProperties authProperties) {
    this.authService = authService;
    this.authProperties = authProperties;
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    AuthSessionIssue issued = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(issued.refreshToken()).toString())
        .body(issued.response());
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    AuthSessionIssue issued = authService.login(request);
    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(issued.refreshToken()).toString())
        .body(issued.response());
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(HttpServletRequest request) {
    String refreshToken = extractRefreshToken(request);
    AuthSessionIssue issued = authService.refresh(refreshToken);

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(issued.refreshToken()).toString())
        .body(issued.response());
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request) {
    String refreshToken = extractRefreshToken(request);
    authService.logout(refreshToken);

    return ResponseEntity.noContent()
        .header(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString())
        .build();
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> me(@AuthenticationPrincipal AuthenticatedUser currentUser) {
    return ResponseEntity.ok(authService.me(currentUser));
  }

  private String extractRefreshToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return null;
    }

    for (Cookie cookie : cookies) {
      if (authProperties.getRefreshCookieName().equals(cookie.getName())) {
        return cookie.getValue();
      }
    }

    return null;
  }

  private ResponseCookie buildRefreshCookie(String refreshToken) {
    return ResponseCookie.from(authProperties.getRefreshCookieName(), refreshToken)
        .httpOnly(true)
        .secure(authProperties.isRefreshCookieSecure())
        .sameSite("Lax")
        .path(authProperties.getRefreshCookiePath())
        .maxAge(Duration.ofSeconds(authProperties.getJwt().getRefreshTtlSeconds()))
        .build();
  }

  private ResponseCookie clearRefreshCookie() {
    return ResponseCookie.from(authProperties.getRefreshCookieName(), "")
        .httpOnly(true)
        .secure(authProperties.isRefreshCookieSecure())
        .sameSite("Lax")
        .path(authProperties.getRefreshCookiePath())
        .maxAge(0)
        .build();
  }
}
