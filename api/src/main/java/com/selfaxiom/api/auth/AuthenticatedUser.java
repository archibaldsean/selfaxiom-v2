package com.selfaxiom.api.auth;

public record AuthenticatedUser(Long id, String username, String email) {
}
