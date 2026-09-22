package com.readMe.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * Refresh tokens are opaque random strings (not JWTs) stored in Redis as
 * refresh_token:<token> -> <email>, with a TTL matching jwt.refresh-expiration-ms.
 * Storing them server-side (unlike access tokens) is what makes them revocable —
 * logout just deletes the Redis key, immediately invalidating that session.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String KEY_PREFIX = "refresh_token:";

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    public String createRefreshToken(String email) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(KEY_PREFIX + token, email, Duration.ofMillis(refreshExpirationMs));
        return token;
    }

    /** Returns the email tied to this token, or null if it doesn't exist / has expired. */
    public String getEmailForToken(String token) {
        return redisTemplate.opsForValue().get(KEY_PREFIX + token);
    }

    public void deleteToken(String token) {
        redisTemplate.delete(KEY_PREFIX + token);
    }

    /** Rotation: invalidate the old token and issue a fresh one for the same user. */
    public String rotateToken(String oldToken, String email) {
        deleteToken(oldToken);
        return createRefreshToken(email);
    }
}

