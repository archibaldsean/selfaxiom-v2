package com.selfaxiom.api.user;

public class UserResponse {

  private final Long id;
  private final String username;
  private final String email;
  private final int pointsBalance;

  public UserResponse(Long id, String username, String email, int pointsBalance) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.pointsBalance = pointsBalance;
  }

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public int getPointsBalance() {
    return pointsBalance;
  }
}
