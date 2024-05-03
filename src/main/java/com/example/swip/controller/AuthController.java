//package com.example.swip.controller;
//
//@RestController
//@RequiredArgsConstructor
//public class AuthController {
//    private final AuthService authService;
//    private final UserService userService;
//
//    @Operation(summary = "USER ID 확인", description = "JWT 토큰 계정과 알맞은 userID를 반환합니다. 헤더 내 Authorization:Bearer ~ 형태의 JWT 토큰을 필요로 합니다.")
//    @GetMapping("/user") // user id 반환
//    public getUserID getUserId(){  // @Validated
//        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if(user != null) {
//            return getUserID.builder()
//                    .user_id(user.getUserId())
//                    .build();
//        }
//        return null;
//    }
//    @PostMapping("/auth/login") // 'test@test.com', 'test' 입력시 로그인 토큰 반환 (UserService 내부 정의)
//    public LoginResponse login(@RequestBody @Validated LoginRequest loginRequest){  // @Validated
//        return authService.attemptLogin(loginRequest.getEmail(), loginRequest.getPassword());
//    }
//
//}