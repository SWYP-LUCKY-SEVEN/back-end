package com.example.swip.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtRefreshResponse {
    private final String accessToken;
    private final String refreshToken;
}
