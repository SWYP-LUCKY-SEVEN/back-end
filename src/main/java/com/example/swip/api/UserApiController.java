package com.example.swip.api;

import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.auth.PostProfileRequest;
import com.example.swip.dto.auth.PostProfileResponse;
import com.example.swip.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @Operation(summary = "회원가입 시 프로필 생성 메소드", description = "회원가입 시 프로필을 생성하는 메소드입니다. 헤더 내 Authorization:Bearer ~ 형태의 JWT 토큰을 필요로 합니다.")
    @PostMapping("/user/profile") // swagger를 위해 변형을 줌
    public PostProfileResponse postUserProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Validated PostProfileRequest postProfileRequest
            ) {
        if(principal == null)
            return null;

        userService.createProfile(principal.getUserId(), postProfileRequest.getProfileImage(), postProfileRequest.getNickname());
        return null;
    }
}
