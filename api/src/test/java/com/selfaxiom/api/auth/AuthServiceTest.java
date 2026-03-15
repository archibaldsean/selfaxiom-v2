package com.selfaxiom.api.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.selfaxiom.api.user.User;
import com.selfaxiom.api.user.UserRepository;
import com.selfaxiom.api.user.UserResponse;
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
    when(userRepository.save(any(User.class))).thenReturn(new User(1L, "archi", "archi@example.com", "hashed-password"));

    UserResponse response = authService.register(request);

    assertEquals(1L, response.getId());
    assertEquals("archi", response.getUsername());
    assertEquals("archi@example.com", response.getEmail());
  }

  @Test
  void loginThrowsOnInvalidPassword() {
    LoginRequest request = new LoginRequest();
    request.setIdentifier("archi");
    request.setPassword("wrong");

    User user = new User(2L, "archi", "archi@example.com", "hashed-password");
    when(userRepository.findByEmail("archi")).thenReturn(Optional.empty());
    when(userRepository.findByUsername("archi")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrong", "hashed-password")).thenReturn(false);

    assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
  }
}
