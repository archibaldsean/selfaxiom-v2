package com.selfaxiom.api.auth;

import java.time.Instant;

public record ParsedRefreshToken(Long userId, String tokenId, Instant expiresAt) {
}
