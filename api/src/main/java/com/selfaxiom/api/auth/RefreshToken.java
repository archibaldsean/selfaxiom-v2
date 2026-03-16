package com.selfaxiom.api.auth;

import java.time.Instant;

public record RefreshToken(String token, String tokenId, Instant expiresAt) {
}
