package com.example.swip.controller;

import com.example.swip.dto.*;
import com.example.swip.entity.User;
import com.example.swip.service.AuthService;
import com.example.swip.service.KakaoOauthService;
import com.example.swip.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/user") // user id 반환
    public getUserID getUserId(){  // @Validated
        User user = userService.findByEmail("test@test.com");
        if(user != null) {
            return getUserID.builder()
                    .user_id(user.getId())
                    .build();
        }
        return null;
    }
    @PostMapping("/auth/login") // 'test@test.com', 'test' 입력시 로그인 토큰 반환 (UserService 내부 정의)
    public LoginResponse login(@RequestBody @Validated LoginRequest loginRequest){  // @Validated
        return authService.attemptLogin(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @PostMapping("/auth/user")
    public String postUser(@RequestBody @Validated AddUserRequest addUserRequest) {
        if (!userService.isDuplicatedUserName(addUserRequest.getEmail())) {
            return authService.addUser(addUserRequest.getEmail(), addUserRequest.getPassword());
        }else
            return "duplicated User Name";
    }
}