package com.example.swip.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtIssuer {
    private final JwtProperties properties;
    @Value("${security.jwt.refresh-token.expire.ttl-hours}")
    private long refreshTokenTtlHours;

    public String issueAT(long userId, String email, String validate, List<String> roles) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(Instant.now().plus(Duration.of(1, ChronoUnit.MINUTES))) // 보통 duration 짧게 하는데 튜토리얼이니까 1day
                .withClaim("e", email)
                .withClaim("v", validate)
                .withClaim("a", roles)
                .withClaim("isRT", false)
                .sign(Algorithm.HMAC256(properties.getSecretKey()));
    }

    public String issueRT(long userId, String email, String validate, List<String> roles) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(Instant.now().plus(Duration.of(refreshTokenTtlHours, ChronoUnit.HOURS)))
                .withClaim("e", email)
                .withClaim("v", validate)
                .withClaim("a", roles)
                .withClaim("isRT", true)
                .sign(Algorithm.HMAC256(properties.getSecretKey()));
    }
}