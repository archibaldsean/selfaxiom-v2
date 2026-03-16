package com.selfaxiom.api.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.selfaxiom.api.auth.AuthExceptions.InvalidCredentialsException;
import com.selfaxiom.api.auth.AuthModels.AuthResponse;
import com.selfaxiom.api.auth.AuthModels.AuthSessionIssue;
import com.selfaxiom.api.auth.AuthModels.LoginRequest;
import com.selfaxiom.api.auth.AuthModels.RefreshToken;
import com.selfaxiom.api.auth.AuthModels.RegisterRequest;
import com.selfaxiom.api.user.User;
import com.selfaxiom.api.user.UserRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtService jwtService;

  @Mock
  private RefreshSessionService refreshSessionService;

  @InjectMocks
  private AuthService authService;

  @Test
  void registerCreatesUserResponse() {
    RegisterRequest request = new RegisterRequest();
    request.setUsername("archi");
    request.setEmail("archi@example.com");
    request.setPassword("password123");

    when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
    when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
    when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed-password");
    when(userRepository.save(any(User.class))).thenReturn(new User(1L, "archi", "archi@example.com", "hashed-password", 0));

    when(jwtService.generateAccessToken(any(User.class))).thenReturn("access-token");
    when(jwtService.generateRefreshToken(any(User.class)))
        .thenReturn(new RefreshToken("refresh-token", "refresh-id", Instant.now().plusSeconds(3600)));

    AuthSessionIssue issued = authService.register(request);
    AuthResponse response = issued.response();

    assertEquals(1L, response.getUser().getId());
    assertEquals("archi", response.getUser().getUsername());
    assertEquals("archi@example.com", response.getUser().getEmail());
    assertEquals("access-token", response.getAccessToken());
    assertEquals("refresh-token", issued.refreshToken());
  }

  @Test
  void loginThrowsOnInvalidPassword() {
    LoginRequest request = new LoginRequest();
    request.setIdentifier("archi");
    request.setPassword("wrong");

    User user = new User(2L, "archi", "archi@example.com", "hashed-password", 0);
    when(userRepository.findByEmail("archi")).thenReturn(Optional.empty());
    when(userRepository.findByUsername("archi")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrong", "hashed-password")).thenReturn(false);

    assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
  }
}
