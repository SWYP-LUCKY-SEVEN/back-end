package com.example.swip.service;

import com.example.swip.dto.KakaoRegisterDto;

public interface KakaoOauthService {

    String getKakaoAccessToken(String code);

    KakaoRegisterDto getKakaoProfile(String accessToken);
}
