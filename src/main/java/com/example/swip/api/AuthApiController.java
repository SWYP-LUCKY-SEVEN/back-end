package com.example.swip.api;

import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.auth.GetUserIDResponse;
import com.example.swip.dto.auth.ValidateTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthApiController {
    @Operation(summary = "USER ID 확인", description = "JWT 토큰 계정과 알맞은 userID를 반환합니다. 헤더 내 Authorization:Bearer ~ 형태의 JWT 토큰을 필요로 합니다.")
    @GetMapping("/auth/user_id") // user id 반환
    public GetUserIDResponse getUserId(@AuthenticationPrincipal UserPrincipal principal){  // Authorization 내 principal 없으면 null 값
        //@AuthenticationPrincipal UserPrincipal principal 는 아래와 동일.
        //UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal != null) {
            return GetUserIDResponse.builder()
                    .user_id(principal.getUserId())
                    .build();
        }
        return null;
    }
    @Operation(summary = "USER ID 확인", description = "JWT 토큰 계정과 알맞은 userID를 반환합니다. 헤더 내 Authorization:Bearer ~ 형태의 JWT 토큰을 필요로 합니다.")
    @GetMapping("/auth/validate/token") // user id 반환
    public ValidateTokenResponse validateToken(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(value = "user_id") Long user_id
    ){
        if(principal == null)
            return null;

        if(principal.getUserId() == user_id) {
            return ValidateTokenResponse.builder()
                    .validated(true)
                    .build();
        }else {
            return ValidateTokenResponse.builder()
                    .validated(false)
                    .build();
        }
    }
}
