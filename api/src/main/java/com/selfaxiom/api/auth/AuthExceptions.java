package com.selfaxiom.api.auth;

public final class AuthExceptions {

  private AuthExceptions() {
  }

  public static class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(String message) {
      super(message);
    }
  }

  public static class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
      super(message);
    }
  }
}
