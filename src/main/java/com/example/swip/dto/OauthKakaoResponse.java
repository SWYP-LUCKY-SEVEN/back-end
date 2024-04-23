package com.example.swip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OauthKakaoResponse {
    private String accessToken;
    private String refreshToken;
}
