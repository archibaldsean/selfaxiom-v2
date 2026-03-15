package com.selfaxiom.api.auth;

import com.selfaxiom.api.user.User;
import com.selfaxiom.api.user.UserRepository;
import com.selfaxiom.api.user.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

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

  public UserResponse login(LoginRequest request) {
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

    return new UserResponse(user.getId(), user.getUsername(), user.getEmail());

  }

}
