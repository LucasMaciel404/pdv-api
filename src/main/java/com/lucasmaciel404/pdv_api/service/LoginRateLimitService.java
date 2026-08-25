package com.lucasmaciel404.pdv_api.service;

import java.util.Collections;

import com.lucasmaciel404.pdv_api.security.loginRateLimit.RateLimitResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
public class LoginRateLimitService {

    private static final String KEY_PREFIX = "rate-limit:login:";

    private static final DefaultRedisScript<String> SCRIPT =
            new DefaultRedisScript<>(
                    """
                            local current = redis.call('INCR', KEYS[1])
                            
                            if current == 1 then
                                redis.call('EXPIRE', KEYS[1], ARGV[1])
                            end
                            
                            local ttl = redis.call('TTL', KEYS[1])
                            
                            return tostring(current) .. ':' .. tostring(ttl)
                            """, String.class
            );

    private final StringRedisTemplate redisTemplate;
    private final long maxAttempts;
    private final long windowSeconds;

    public LoginRateLimitService(
            StringRedisTemplate redisTemplate,
            @Value("${security.rate-limit.login.max-attempts}")
            long maxAttempts,
            @Value("${security.rate-limit.login.window-seconds}")
            long windowSeconds
    ) {
        this.redisTemplate = redisTemplate;
        this.maxAttempts = maxAttempts;
        this.windowSeconds = windowSeconds;
    }

    public RateLimitResult consume(String clientIdentifier) {
        String key = KEY_PREFIX + clientIdentifier;

        String result = redisTemplate.execute(
                SCRIPT,
                Collections.singletonList(key),
                String.valueOf(windowSeconds)
        );

        if (result == null) {
            throw new IllegalStateException(
                    "O Redis não retornou o resultado do rate limit"
            );
        }

        String[] values = result.split(":");

        long attempts = Long.parseLong(values[0]);
        long ttl = Long.parseLong(values[1]);

        boolean allowed = attempts <= maxAttempts;

        long remainingAttempts = Math.max(
                0,
                maxAttempts - attempts
        );

        long retryAfterSeconds = Math.max(0, ttl);

        return new RateLimitResult(
                allowed,
                remainingAttempts,
                retryAfterSeconds
        );
    }
}