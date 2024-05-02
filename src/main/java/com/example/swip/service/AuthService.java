package com.example.swip.service;

import com.example.swip.dto.KakaoRegisterDto;
import com.example.swip.dto.LoginResponse;
import com.example.swip.dto.OauthKakaoResponse;
import com.example.swip.entity.User;

public interface AuthService {
    public LoginResponse attemptLogin(String email, String password);
    public OauthKakaoResponse oauthLogin(User user);

    public String addUser(String email, String password);

    public User kakaoRegisterUser(KakaoRegisterDto kakaoRegisterDto);
}