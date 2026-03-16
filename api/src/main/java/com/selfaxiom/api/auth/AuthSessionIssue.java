package com.selfaxiom.api.auth;

public record AuthSessionIssue(AuthResponse response, String refreshToken) {
}
