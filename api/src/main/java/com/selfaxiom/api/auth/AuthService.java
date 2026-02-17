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
    if (userRepository.existsByEmail(request.getEmail())
        || userRepository.existsByUsername(request.getUsername())) {
      throw new DuplicateUserException("Username or email already exists");
    }
    User user = new User(null, request.getUsername(), request.getEmail(),
        passwordEncoder.encode(request.getPassword()));
    User savedUser = userRepository.save(user);
    return new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
  }

}
