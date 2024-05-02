package com.example.swip.api;

import com.example.swip.dto.KakaoRegisterDto;
import com.example.swip.dto.OauthKakaoResponse;
import com.example.swip.entity.User;
import com.example.swip.service.AuthService;
import com.example.swip.service.KakaoOauthService;
import com.example.swip.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OauthApiController {
    private final AuthService authService;
    private final KakaoOauthService kakaoOauthService;

    @GetMapping("/oauth/kakao")
    public OauthKakaoResponse kakaoCalllback(@RequestParam(value = "code") String code) {
        String accessToken = kakaoOauthService.getKakaoAccessToken(code);

        KakaoRegisterDto kakaoRegisterDto = kakaoOauthService.getKakaoProfile(accessToken);

        //회원가입 후, user 정보를 반환함. 회원가입이 되어있다면 바로 user정보를 반환함
        User user = authService.kakaoRegisterUser(kakaoRegisterDto);

        System.out.println("getValidate : " + user.getValidate());
        System.out.println("getRole : " + user.getRole());

        // JWT 토큰, 회원가입 정보
        return authService.oauthLogin(user);
    }
}
