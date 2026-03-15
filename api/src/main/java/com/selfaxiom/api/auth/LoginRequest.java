package com.selfaxiom.api.auth;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

  @NotBlank
  private String identifier; // can be email or username

  @NotBlank
  private String password;

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
