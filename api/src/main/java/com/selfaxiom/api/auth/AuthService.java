package com.selfaxiom.api.auth;

import com.selfaxiom.api.auth.AuthExceptions.DuplicateUserException;
import com.selfaxiom.api.auth.AuthExceptions.InvalidCredentialsException;
import com.selfaxiom.api.auth.AuthModels.AuthResponse;
import com.selfaxiom.api.auth.AuthModels.AuthSessionIssue;
import com.selfaxiom.api.auth.AuthModels.AuthenticatedUser;
import com.selfaxiom.api.auth.AuthModels.LoginRequest;
import com.selfaxiom.api.auth.AuthModels.ParsedRefreshToken;
import com.selfaxiom.api.auth.AuthModels.RefreshToken;
import com.selfaxiom.api.auth.AuthModels.RegisterRequest;
import com.selfaxiom.api.user.User;
import com.selfaxiom.api.user.UserRepository;
import com.selfaxiom.api.user.UserResponse;
import java.util.Optional;
import java.util.Objects;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final RefreshSessionService refreshSessionService;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
      RefreshSessionService refreshSessionService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.refreshSessionService = refreshSessionService;
  }

  public AuthSessionIssue register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())
        || userRepository.existsByUsername(request.getUsername())) {
      throw new DuplicateUserException("Username or email already exists");
    }
    User user = new User(null, request.getUsername(), request.getEmail(),
        passwordEncoder.encode(request.getPassword()), 0);
    User savedUser = userRepository.save(user);
    return issueSession(savedUser);
  }

  public AuthSessionIssue login(LoginRequest request) {
    String identifier = request.getIdentifier();
    Optional<User> userOpt = userRepository.findByEmail(identifier);
    if (userOpt.isEmpty()) {
      userOpt = userRepository.findByUsername(identifier);
    }
    if (userOpt.isEmpty()) {
      throw new InvalidCredentialsException("Invalid credentials");
    }

    User user = userOpt.get();

    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
      throw new InvalidCredentialsException("Invalid credentials");
    }

    return issueSession(user);
  }

  public AuthSessionIssue refresh(String refreshTokenRaw) {
    if (refreshTokenRaw == null || refreshTokenRaw.isBlank()) {
      throw new InvalidCredentialsException("Missing refresh token");
    }

    ParsedRefreshToken parsed = jwtService.parseRefreshToken(refreshTokenRaw);
    refreshSessionService.assertValid(parsed.userId(), parsed.tokenId());

    User user = userRepository.findById(parsed.userId())
        .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

    RefreshToken nextRefresh = jwtService.generateRefreshToken(user);
    refreshSessionService.rotate(parsed.userId(), parsed.tokenId(), user, nextRefresh.tokenId(),
        nextRefresh.expiresAt());

    UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getPointsBalance());
    String accessToken = jwtService.generateAccessToken(user);
    return new AuthSessionIssue(new AuthResponse(userResponse, accessToken), nextRefresh.token());
  }

  public void logout(String refreshTokenRaw) {
    if (refreshTokenRaw == null || refreshTokenRaw.isBlank()) {
      return;
    }

    try {
      ParsedRefreshToken parsed = jwtService.parseRefreshToken(refreshTokenRaw);
      refreshSessionService.revokeIfExists(parsed.userId(), parsed.tokenId());
    } catch (RuntimeException ignored) {
    }
  }

  public UserResponse me(AuthenticatedUser currentUser) {
    if (currentUser == null || currentUser.id() == null) {
      throw new InvalidCredentialsException("Invalid credentials");
    }

    User user = userRepository.findById(currentUser.id())
        .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
    return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getPointsBalance());
  }

  private AuthSessionIssue issueSession(User user) {
    Objects.requireNonNull(user.getId(), "User id is required to issue session");
    RefreshToken refreshToken = jwtService.generateRefreshToken(user);
    refreshSessionService.create(user, refreshToken.tokenId(), refreshToken.expiresAt());
    String accessToken = jwtService.generateAccessToken(user);
    UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getPointsBalance());
    return new AuthSessionIssue(new AuthResponse(userResponse, accessToken), refreshToken.token());

  }

}
