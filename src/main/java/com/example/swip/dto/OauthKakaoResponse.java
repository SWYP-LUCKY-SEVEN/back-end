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

//@Data
//public class OauthToken {
//    private String access_token;
//    private String token_type;
//    private String id_token; // ID 토큰 값. OpenID Connect 확장 기능을 통해 발급되는 ID 토큰, Base64 인코딩 된 사용자 인증 정보 포함
//    private String refresh_token;
//    private int expires_in; //액세스 토큰과 ID 토큰의 만료 시간(초) (액세스 토큰과 ID 토큰의 만료 시간은 동일)
//    private String scope;
//    private int refresh_token_expires_in; //	리프레시 토큰 만료 시간(초)
//}