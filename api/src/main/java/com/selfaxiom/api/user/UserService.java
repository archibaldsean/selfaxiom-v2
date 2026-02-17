package com.selfaxiom.api.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserResponse getById(Long id) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  public UserResponse update(Long id, UpdateUserRequest request) {
    throw new UnsupportedOperationException("Not implemented yet");
  }
}
