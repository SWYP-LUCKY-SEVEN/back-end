package com.example.swip.api;

import com.example.swip.dto.oauth.KakaoRegisterDto;
import com.example.swip.dto.oauth.OauthKakaoResponse;
import com.example.swip.entity.User;
import com.example.swip.service.AuthService;
import com.example.swip.service.KakaoOauthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OauthApiController {
    private final AuthService authService;
    private final KakaoOauthService kakaoOauthService;

    @Operation(summary = "카카오를 사용한 로그인", description = "카카오에서 발급된 인증 Code를 통해, JWT 토큰 및 회원 정보를 반환합니다. 프로필 등록 미진행 회원일 경우 join_date가 null로 반환됩니다. 또한 code가 유효하지 않을 경우 null을 반환됩니다.")
    @GetMapping("/oauth/kakao")
    public OauthKakaoResponse kakaoCalllback(@RequestParam(value = "code") String code) {
        String accessToken = kakaoOauthService.getKakaoAccessToken(code);

        if(accessToken != "") {
            KakaoRegisterDto kakaoRegisterDto = kakaoOauthService.getKakaoProfile(accessToken);
            //회원가입 후, user 정보를 반환함. 회원가입이 되어있다면 바로 user정보를 반환함
            User user = authService.kakaoRegisterUser(kakaoRegisterDto);
            return authService.oauthLogin(user);
        }
        return null;
    }
}
