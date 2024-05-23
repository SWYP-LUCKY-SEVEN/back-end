package com.example.swip.api;

import com.example.swip.config.JwtIssuer;
import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.EvaluationRequest;
import com.example.swip.dto.auth.AddUserRequest;
import com.example.swip.dto.todo.StudyMBOResponse;
import com.example.swip.dto.user.UserMainProfileDto;
import com.example.swip.entity.User;
import com.example.swip.service.*;
import com.mysema.commons.lang.Pair;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class TesterApiController {
    private final AuthService authService;
    private final UserService userService;
    private final StudyService studyService;
    private final UserStudyService userStudyService;
    private final UserWithdrawalService userWithdrawalService;
    private final ChatServerService chatServerService;
    private final JwtIssuer jwtIssuer;
    @Operation(summary = "모든 USER ID 및 정보 확인 (테스트용 API)", description = "가입된 모든 user의 Id 및 정보를 반환합니다.")
    @GetMapping("/test/user/all")
    public ResponseEntity<List<UserMainProfileDto>> getAllUserId(){  // Authorization 내 principal 없으면 null 값
        List<Long> userList = authService.getAllUserId();
        List<UserMainProfileDto>  list = userList.stream().map(user_id -> {
             User user = userService.findUserById(user_id);
             return userService.getMainProfile(user);
        }).collect(Collectors.toList());

        return ResponseEntity.status(200).body(list);
    };
    @Operation(summary = "테스트 유저 회원 가입", description = "테스트용 유저를 가입 시킨다.")
    @PostMapping("/test/user") // user id 반환
    public ResponseEntity<DefaultResponse> postUser(
            @RequestBody AddUserRequest addUserRequest
    ){
        if (userService.findByEmail(addUserRequest.getEmail()) != null)
            return ResponseEntity.status(400).body(
                    DefaultResponse.builder()
                            .message("exist Email")
                            .build()
            );
        Long user_id = userService.saveTestUser(addUserRequest);
        return ResponseEntity.status(200).body(
                DefaultResponse.builder()
                        .message("user ID : " + user_id.toString())
                        .build()
        );
    }
    @Operation(summary = "테스트 유저 JWT 발급", description = "테스트용 유저를 가입 시킨다.")
    @PostMapping("/test/jwt")
    public ResponseEntity<DefaultResponse> postJWT(
        @RequestParam Long user_id
    ){  // Authorization 내 principal 없으면 null 값
        User user = userService.findUserById(user_id);
        if(user  == null)
            return ResponseEntity.status(404).body(DefaultResponse.builder()
                            .message("존재하지 않는 ID")
                    .build());
        List<String> list = new LinkedList<>(Arrays.asList(user.getRole()));

        String jwt = jwtIssuer.issue(user.getId(),user.getEmail(),user.getValidate(),list);
        return ResponseEntity.status(200).body(DefaultResponse.builder()
                .message("Bearer "+jwt)
                .build());
    }
    @Operation(summary = "JWT 정보 확인")
    @GetMapping("/test/jwt")
    public DefaultResponse getJWT(
            @AuthenticationPrincipal UserPrincipal userPrincipal // 권한 인증
    ) {
        if(userPrincipal == null)
            return null;
        String message = "ID = " + userPrincipal.getUserId();
        message.concat("검증방식 = " + userPrincipal.getValidate());
        message.concat("Email = " + userPrincipal.getEmail());

        return DefaultResponse.builder()
                .message(message)
                .build();
    }
    @Operation(summary = "회원 즉시 삭제 [테스트용] (운영중인 스터디 삭제)", description = "회원을 즉시 삭제합니다. 관련된 모든 스터디도 삭제됩니다.")
    @DeleteMapping("/test/user") // user id 반환
    public ResponseEntity<DefaultResponse> deleteUserByUserId(@AuthenticationPrincipal UserPrincipal principal){  // Authorization 내 principal 없으면 null 값
        if(principal == null)
            return ResponseEntity.status(401).build();

        Pair<Integer, Long> result = userWithdrawalService.deleteUser(principal.getUserId(), true);

        if(result.getSecond() == null) {
            return ResponseEntity.status(401).body(
                    DefaultResponse.builder()
                            .message("등록되지 않은 JWT")
                            .build()
            );
        }
        Pair<String, Integer> response = chatServerService.deleteUser(result.getSecond());

        String status_text = "";
        if(result.getFirst() == 201)
            status_text = "Delete success!";

        return ResponseEntity.status(result.getFirst()).body(DefaultResponse.builder()
                .message(status_text +" chat server response : "+response.getFirst() + response.getSecond().toString())
                .build());
    }

    @Operation(summary = "회원 탈퇴 진행 (운영중인 스터디 삭제)", description = "JWT 토큰 해당하는 계정에 탈퇴 과정을 진행합니다. 운영중인 스터디는 모두 사라집니다.")
    @PatchMapping("/user/withdrawal/forcing") //
    public ResponseEntity<DefaultResponse> withdrawalUserWithDeleteStudy(@AuthenticationPrincipal UserPrincipal principal) {
        if(principal == null)
            return ResponseEntity.status(401).build();

        Pair<Integer, Long> result = userWithdrawalService.withdrawal(principal.getUserId(), true);

        if(result.getSecond() == null) {
            return ResponseEntity.status(401).body(
                    DefaultResponse.builder()
                            .message("등록되지 않은 JWT")
                            .build()
            );
        }
        Pair<String, Integer> response = chatServerService.deleteUser(result.getSecond());

        String status_text = "";
        if(result.getFirst() == 201)
            status_text = "Delete success!";

        return ResponseEntity.status(result.getFirst()).body(DefaultResponse.builder()
                .message(status_text +" chat server response : "+response.getFirst() + response.getSecond().toString())
                .build());
    }

    @Operation(summary = "특정 유저 스터디 참가/신청 (테스트용 API)",
            description = "유저 JWT 필요 ")
    @PostMapping("/test/join/{study_id}")
    public ResponseEntity testMatchStudy(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("study_id") Long studyId
    ) {
        return studyService.joinStudy(studyId, userPrincipal.getUserId(), userPrincipal.getToken());
    }

    @Operation(summary = "스터디 상태 강제 변경 (테스트용 API)",
            description = "JWT 상관 없이 변경\n" +
                    "- status = before, progress, done")
    @PatchMapping("/test/study/{study_id}/status")
    public ResponseEntity<DefaultResponse> testUpdateStudystatus(
            @PathVariable("study_id") Long studyId,
            @RequestParam String status
    ) {
        Long userId = userStudyService.getOwnerbyStudyId(studyId);
        int respone_status = studyService.updateStudyStatus(studyId, userId, status);
        return ResponseEntity.status(respone_status).body(DefaultResponse.builder()
                .message("성공적입니다.")
                .build());
    }


    @Operation(summary = "회원 평가 진행 (셀프 평가 허용)", description = "score 값은 0~100까지 입니다." +
            "- fromId: 평가하는 ID\n" +
            "- toId : 평가 당하는 ID")
    @PostMapping("/test/user/evaluation") //
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

    @GetMapping("/study/localDataTime")
    private LocalDateTime printServerTime(){
        LocalDateTime now = LocalDateTime.now();
        return now;
    }

    @PostMapping("/study/token")
    public String getTokenString(
            @AuthenticationPrincipal UserPrincipal userPrincipal // 권한 인증
    ) {
        System.out.println("userPrincipal.getUserId() = " + userPrincipal.getUserId());
        System.out.println("userPrincipal.getValidate() = " + userPrincipal.getValidate());
        System.out.println("userPrincipal.getEmail() = " + userPrincipal.getEmail());
        System.out.println("userPrincipal.getToken() = " + userPrincipal.getToken());
        if(userPrincipal == null)
            return null;
        return userPrincipal.getToken();
    }
}