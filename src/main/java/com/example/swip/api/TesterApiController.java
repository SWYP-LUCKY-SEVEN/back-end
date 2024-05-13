package com.example.swip.api;

import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.EvaluationRequest;
import com.example.swip.entity.User;
import com.example.swip.service.AuthService;
import com.example.swip.service.StudyService;
import com.example.swip.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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


    @Operation(summary = "특정 유저 스터디 참가 (테스트용 API)",
            description = "현재 즉시 참가 기능만 지원.\n" +
                    "존재하는 user id는 auth/")
    @PostMapping("/study/{study_id}/status/update")
    public ResponseEntity testUpdateStudystatus(
            @PathVariable("study_id") Long studyId
    ) {
        studyService.progressStartStudy(LocalDate.now());
        return null;
    }


    @Operation(summary = "회원 평가 진행 (셀프 평가 허용)", description = "score 값은 0~100까지 입니다." +
            "- fromId: 평가하는 ID\n" +
            "- toId : 평가 당하는 ID")
    @PostMapping("/user/evaluation/test") //
    public ResponseEntity<DefaultResponse> postEvaluationUserTest(
            @RequestParam Long fromId,
            @RequestBody EvaluationRequest evaluationRequest
    ) {
        User test = userService.findUserById(fromId);
        if(test == null)
            return ResponseEntity.status(400).body(
                    DefaultResponse.builder()
                            .message("올바르지 않은 fromId 입니다.")
                            .build());

        if(!userService.evaluationUser(evaluationRequest.getTo_id(),
                fromId,
                evaluationRequest.getScore().intValue()))
            return ResponseEntity.status(400).body(
                    DefaultResponse.builder()
                            .message("score 값이 범위를 벗어났습니다. 0 부터 100 까지")
                            .build());

        return ResponseEntity.status(201).body(
                DefaultResponse.builder()
                        .message("평가가 성공적으로 저장되었습니다.")
                        .build());
    }
}
