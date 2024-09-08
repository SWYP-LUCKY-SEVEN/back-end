package com.example.swip.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

    @Value("${security.jwt.refresh-token.expire.ttl-second}")
    private long ttlSeconds;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean isTokenValid(String token) {
        // Redis에서 토큰 검증
        return redisTemplate.hasKey("refreshToken:" + token);
    }

    public void addToken(String token) {
        // Redis에 토큰 저장
        redisTemplate.opsForValue().set("refreshToken:" + token, "valid", ttlSeconds);
    }

    public void removeToken(String token) {
        // Redis에서 토큰 삭제
        redisTemplate.delete("refreshToken:" + token);
    }
}
