package com.lucasmaciel404.pdv_api.security.loginRateLimit;

public record RateLimitResult(
        boolean allowed,
        long remainingAttempts,
        long retryAfterSeconds
) {
}