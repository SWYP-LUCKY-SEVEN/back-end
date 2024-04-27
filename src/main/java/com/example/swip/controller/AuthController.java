package com.example.swip.controller;

import com.example.swip.dto.*;
import com.example.swip.entity.User;
import com.example.swip.service.AuthService;
import com.example.swip.service.OauthService;
import com.example.swip.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final OauthService oauthService;

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

    //https://kauth.kakao.com/oauth/authorize?client_id=4272a0b3892816ffa5ef615c430ca7a9&redirect_uri=http://localhost:3000/login/kakao&response_type=code
    @GetMapping("/oauth/kakao")
    public OauthKakaoResponse kakaoCalllback(@RequestParam(value = "code") String code) {
        String accessToken = oauthService.getKakaoAccessToken(code);

        KakaoRegisterDto kakaoRegisterDto = oauthService.getKakaoProfile(accessToken);

        //회원가입 후, user 정보를 반환함. 회원가입이 되어있다면 바로 user정보를 반환함
        User user = authService.kakaoRegisterUser(kakaoRegisterDto);

        System.out.println("getValidate : " + user.getValidate());
        System.out.println("getRole : " + user.getRole());

        return authService.oauthLogin(user);
        // JWT 토큰, 회원가입 상태, 회원가입 정보
        //return authService.oauthLogin(user.getEmail(), user.getValidate());
    }

    //요청의 인증 code를 받아, Kakao에서 accessToken 및 회원 정보를 발급받아 제공.
    @PostMapping("/oauth/kakao")
    public String postKakaoToken(@RequestBody @Validated OauthKakaoRequest oauthKakaoRequest) {
        System.out.println("code : " + oauthKakaoRequest.getCode());
        return oauthService.getKakaoAccessToken(oauthKakaoRequest.getCode());
    }
}