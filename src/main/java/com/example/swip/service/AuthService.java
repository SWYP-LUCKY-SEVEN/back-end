package com.example.swip.service;

import com.example.swip.config.security.UserPrincipal;
import com.example.swip.dto.auth.JwtRefreshResponse;
import com.example.swip.dto.auth.ValidateTokenResponse;
import com.example.swip.dto.oauth.KakaoRegisterDto;
import com.example.swip.dto.auth.LoginResponse;
import com.example.swip.dto.oauth.OauthKakaoResponse;
import com.example.swip.entity.User;

import java.util.List;

public interface AuthService {
    public LoginResponse attemptLogin(String email, String password);
    public OauthKakaoResponse oauthLogin(User user);

    public String addUser(String email, String password);
    public User kakaoRegisterUser(KakaoRegisterDto kakaoRegisterDto);

    public JwtRefreshResponse JwtRefresh(UserPrincipal principal);
    public Boolean JwtLogout(UserPrincipal principal);

    public ValidateTokenResponse compareJWTWithId(String jwt, long user_id);
    public List<Long> getAllUserId();
}