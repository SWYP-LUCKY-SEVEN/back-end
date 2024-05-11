package com.example.swip.api;

import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.DefaultResponse;
import com.example.swip.service.AuthService;
import com.example.swip.service.StudyService;
import com.example.swip.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TesterApiController {
    private final AuthService authService;
    private final UserService userService;
    private final StudyService studyService;

    @Operation(summary = "모든 USER ID 확인 (테스트용 API)", description = "가입된 모든 user의 Id를 반환합니다.")
    @GetMapping("/auth/user_id/all") // user id 반환
    public ResponseEntity<List<Long>> getAllUserId(@AuthenticationPrincipal UserPrincipal principal){  // Authorization 내 principal 없으면 null 값
        if(principal != null) {

            return ResponseEntity.status(200).body(authService.getAllUserId());
        }
        return ResponseEntity.status(403).build();
    }

    @Operation(summary = "회원 탈퇴 JWT (테스트용 API)", description = "JWT 토큰 해당하는 계정을 지웁니다.")
    @DeleteMapping("/auth/user_id") // user id 반환
    public String deleteUserById(@AuthenticationPrincipal UserPrincipal principal){  // Authorization 내 principal 없으면 null 값
        if(principal != null)
            return userService.deleteUser(principal.getUserId());
        return "need JWT in Authorization";
    }

    @Operation(summary = "회원 탈퇴 (테스트용 API)", description = "입력된 ID의 계정을 지웁니다.")
    @DeleteMapping("/auth/{user_id}") // user id 반환
    public String deleteUserByUserId(
            @PathVariable("user_id") Long user_id
    ){  // Authorization 내 principal 없으면 null 값
        return userService.deleteUser(user_id);
    }

    @Operation(summary = "특정 유저 스터디 참가 (테스트용 API)",
            description = "현재 즉시 참가 기능만 지원.\n" +
                    "존재하는 user id는 auth/")
    @PostMapping("/study/join/{study_id}/{user_id}")
    public ResponseEntity testMatchStudy(
            @PathVariable("study_id") Long studyId,
            @PathVariable("user_id") Long userId
    ) {
        return studyService.joinStudy(studyId, userId);
    }
}
