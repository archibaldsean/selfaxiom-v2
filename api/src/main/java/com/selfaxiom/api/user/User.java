package com.selfaxiom.api.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "users", schema = "selfaxiom")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "username", nullable = false, unique = true)
  private String username;
  @Column(name = "email", nullable = false, unique = true)
  private String email;
  @Column(name = "password_hash", nullable = false)
  private String passwordHash;
  @Column(name = "points_balance", nullable = false)
  private int pointsBalance;

  // Constructor

  public User() {
  }

  public User(Long id, String username, String email, String passwordHash, int pointsBalance) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.passwordHash = passwordHash;
    this.pointsBalance = pointsBalance;
  }

  // Getters

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public int getPointsBalance() {
    return pointsBalance;
  }

  // Setters

  public void setUsername(String username) {
    this.username = username;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public void setPointsBalance(int pointsBalance) {
    this.pointsBalance = pointsBalance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    User user = (User) o;
    return Objects.equals(id, user.id) && Objects.equals(username, user.username)
        && Objects.equals(email, user.email) && Objects.equals(passwordHash, user.passwordHash)
        && pointsBalance == user.pointsBalance;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, email, passwordHash, pointsBalance);
  }
}
