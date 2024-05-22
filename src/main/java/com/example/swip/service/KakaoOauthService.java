package com.example.swip.service;

import com.example.swip.dto.oauth.KakaoRegisterDto;
import com.mysema.commons.lang.Pair;

public interface KakaoOauthService {

    String getKakaoAccessToken(String code);

    Pair<KakaoRegisterDto, Long> getKakaoProfile(String accessToken);

    Long logOutKakao(Long kakaoUserId);
}
