package com.example.swip.api;

import com.example.swip.config.security.JwtDecoder;
import com.example.swip.config.security.UserPrincipal;
import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.auth.GetUserIDResponse;
import com.example.swip.dto.auth.JwtRefreshResponse;
import com.example.swip.dto.auth.LoginResponse;
import com.example.swip.dto.auth.ValidateTokenResponse;
import com.example.swip.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthApiController {
    private final AuthService authService;
    private final JwtDecoder jwtDecoder;

    @Operation(summary = "로드밸런스 상태 체크용", description = "")
    @GetMapping("/") // user id 반환
    public ResponseEntity<DefaultResponse> healthcheck(){
        return ResponseEntity.status(200).build();
    }
    @Operation(summary = "USER ID 확인", description = "JWT 토큰 계정과 알맞은 userID를 반환합니다. 헤더 내 Authorization:Bearer ~ 형태의 JWT 토큰을 필요로 합니다.")
    @GetMapping("/auth/user_id") // user id 반환
    public ResponseEntity<GetUserIDResponse> getUserId(@AuthenticationPrincipal UserPrincipal principal){  // Authorization 내 principal 없으면 null 값
        //@AuthenticationPrincipal UserPrincipal principal 는 아래와 동일.
        //UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal != null) {
            return ResponseEntity.status(200).body(GetUserIDResponse.builder()
                    .user_id(principal.getUserId())
                    .build());
        }
        return ResponseEntity.status(403).build();
    }

    @Operation(summary = "JWT Access Token 재발급", description = "JWT가 만료되었을 경우, Refresh Token을 사용하여 재발급합니다.")
    @PostMapping("/auth/refresh/token")
    public ResponseEntity<JwtRefreshResponse> refreshJwt(
            @AuthenticationPrincipal UserPrincipal principal
    ){
        JwtRefreshResponse result  = authService.JwtRefresh(principal);
        if (result != null) {
            return ResponseEntity.status(201).body(result);
        }
        return ResponseEntity.status(401).build();
    }

    @Operation(summary = "JWT 로그아웃", description = "Refresh 토큰을 통해 요청하면, 해당 Refresh 토큰을 만료시킵니다.")
    @DeleteMapping("/auth/logout")
    public ResponseEntity<DefaultResponse> logoutJwt(
            @AuthenticationPrincipal UserPrincipal principal
    ){
        Boolean result = authService.JwtLogout(principal);
        if (result != null) {
            return ResponseEntity.status(200).body(
                    DefaultResponse.builder()
                            .message("success")
                            .build());
        }
        return ResponseEntity.status(401).build();
    }

    @Operation(summary = "JWT 검증 without HTTP header", description = "JWT가 userID와 일치하는지 확인합니다.")
    @GetMapping("/auth/validate/token")
    public ResponseEntity<ValidateTokenResponse> validateTokenTwoParam(
            @RequestParam(value = "token") String jwt,
            @RequestParam(value = "user_id") Long user_id
    ){
        return ResponseEntity.status(200).body(authService.compareJWTWithId(jwt, user_id));
    }
}
