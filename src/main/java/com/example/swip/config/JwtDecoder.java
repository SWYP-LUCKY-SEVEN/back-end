package com.example.swip.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtDecoder {
    private final JwtProperties properties;
    public DecodedJWT decode(String token) {
        return JWT.require(Algorithm.HMAC256(properties.getSecretKey())) //JWTVerifier 기본 알고리즘 선택.
                .build()   //JWTVerifier 생성 (JWT 검증자)
                .verify(token); // JWTDecoder를 통해 디코딩
        // 최종적으로 DecodedJWT 반환. getToken, getHeader, getPayload, getSignature 메소드 존재
    }
}