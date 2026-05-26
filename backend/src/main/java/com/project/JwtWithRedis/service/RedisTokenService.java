package com.project.JwtWithRedis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisTokenService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_WHITELIST_PREFIX = "refresh:";
    private static final String ACCESS_BLACKLIST_PREFIX = "blacklist:";

    public void whitelistRefreshToken(String email, String refreshToken, long ttlInMs) {
        String key = REFRESH_WHITELIST_PREFIX + email;
        redisTemplate.opsForValue().set(key, refreshToken, ttlInMs, TimeUnit.MILLISECONDS);
    }

    public String getWhitelistedRefreshToken(String email) {
        String key = REFRESH_WHITELIST_PREFIX + email;
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(String email) {
        String key = REFRESH_WHITELIST_PREFIX + email;
        redisTemplate.delete(key);
    }

    public void blacklistAccessToken(String accessToken, long remainingTtlInMs) {
        String key = ACCESS_BLACKLIST_PREFIX + accessToken;
        // We save the value "revoked" since the presence of the key itself is the marker
        redisTemplate.opsForValue().set(key, "revoked", remainingTtlInMs, TimeUnit.MILLISECONDS);
    }

    public boolean isAccessTokenBlacklisted(String accessToken) {
        String key = ACCESS_BLACKLIST_PREFIX + accessToken;
        Boolean hasKey = redisTemplate.hasKey(key);
        return hasKey != null && hasKey;
    }

}
