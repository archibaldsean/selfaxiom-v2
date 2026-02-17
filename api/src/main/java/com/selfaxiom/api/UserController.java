package com.selfaxiom.api;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequestMapping("/api/users")
@RestController
public class UserController {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  public UserController(PasswordEncoder passwordEncoder, UserRepository UserRepository) {
    this.userRepository = UserRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping()
  public ResponseEntity<String> createUser(@RequestBody User user) {
    if (userRepository.findByUsername(user.getUsername()).isPresent()
        || userRepository.findByEmail(user.getEmail()).isPresent()) {
      return ResponseEntity.status(409).body("conflict");
    }

    String passwordHash = passwordEncoder.encode(user.getPasswordHash());

    User newUser = new User(null, user.getUsername(), user.getEmail(), passwordHash);
    userRepository.save(newUser);

    return ResponseEntity.status(201).body("created");

  }

  @PutMapping("/{id}")
  public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User user) {
    User existingUser = userRepository.findById(id).orElse(null);
    if (existingUser == null) {
      return ResponseEntity.status(404).body("not found");
    }
    User updatedUser = new User(id, user.getUsername(), user.getEmail(), user.getPasswordHash());
    userRepository.save(updatedUser);
    return ResponseEntity.status(200).body("updated");
  }

  @GetMapping("/{id}")
  public ResponseEntity<String> getUser(@PathVariable Long id) {
    User user = userRepository.findById(id).orElse(null);
    if (user == null) {
      return ResponseEntity.status(404).body("not found");
    }

    return ResponseEntity.status(200).body("found");
  }

}
