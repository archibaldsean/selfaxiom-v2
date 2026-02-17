package com.selfaxiom.api.auth;

import com.selfaxiom.api.user.User;
import com.selfaxiom.api.user.UserRepository;
import com.selfaxiom.api.user.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UserResponse register(RegisterRequest request) {
    throw new UnsupportedOperationException("Not implemented yet");
  }
}
