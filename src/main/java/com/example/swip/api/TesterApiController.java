package com.example.swip.api;

import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.EvaluationRequest;
import com.example.swip.dto.JoinRequest.JoinRequestResponse;
import com.example.swip.dto.auth.AddUserRequest;
import com.example.swip.dto.user.UserMainProfileDto;
import com.example.swip.entity.User;
import com.example.swip.entity.enumtype.JoinStatus;
import com.example.swip.service.*;
import com.mysema.commons.lang.Pair;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    private final JoinRequestService joinRequestService;
    private final TestService testService;
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
    @Operation(summary = "유저 ID로 테스트용 JWT 발급 [테스트]", description = "테스트용 유저를 가입 시킨다.")
    @PostMapping("/test/jwt")
    public ResponseEntity<DefaultResponse> postJWT(
        @RequestParam Long user_id
    ){
        String jwt = testService.getJWTByUserID(user_id);
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
    public ResponseEntity<DefaultResponse> deleteUserByUserId(
            @RequestParam Long userId
    ){
        Pair<Integer, Long> result = userWithdrawalService.deleteUser(userId, true);
        if(result.getSecond() == null) {
            return ResponseEntity.status(401).body(
                    DefaultResponse.builder()
                            .message("등록되지 않은 유저 ID")
                            .build()
            );
        }
        int status = chatServerService.deleteUser(result.getSecond());

        return ResponseEntity.status(status).build();
    }

    //
    // 스터디 요청 관련
    //
    @Operation(summary = "특정 유저 스터디 참가/신청 (테스트용 API)",
            description = "유저 JWT 필요 ")
    @PostMapping("/test/joinRequest/{study_id}")
    public ResponseEntity testJoinRequestStudy(
            @PathVariable("study_id") Long studyId,
            @RequestParam Long userId
    ) {
        String jwt = testService.getJWTByUserID(userId);
        return studyService.joinStudy(studyId, userId, jwt);
    }

    @Operation(summary = "특정 스터디 신청 내역 조회 [테스트]")
    @GetMapping("/test/joinRequest/{study_id}")
    public ResponseEntity<Result> getJoinRequestsByStudyId(
            @PathVariable("study_id") Long studyId
    )
    {
        Long findStudyOwner = userStudyService.getOwnerbyStudyId(studyId);
        if (findStudyOwner == null)
            return ResponseEntity.status(403).body(new Result(null, "스터디를 찾을 수 없음"));

        //방장이면 확인 가능하도록
        List<JoinRequestResponse> responses= joinRequestService.getAllByStudyId(studyId);
        //dto 변환 & return
        return ResponseEntity.status(200).body(new Result(responses, "성공"));
    }

    @Operation(summary = "신청 수락 (테스트용)")
    @PostMapping("/test/joinRequest/accept")
    private ResponseEntity<String> acceptJoinRequest(
            @RequestParam Long studyId,
            @RequestParam Long userId
    )
    {
        //꽉찬 스터디의 경우 수락 불가
        boolean isFull = studyService.isAlreadyFull(studyId);
        if(isFull){
            return ResponseEntity.status(200).body("참여 인원이 꽉 찼습니다.");
        }
        //이미 신청 수락/거부한 경우(더블체크)
        JoinStatus joinStatus = joinRequestService.checkJoinStatusById(studyId, userId);
        if(joinStatus != null) {
            if (joinStatus.equals(JoinStatus.Approved)) {
                return ResponseEntity.status(200).body("이미 수락된 사용자입니다.");
            } else if (joinStatus.equals(JoinStatus.Rejected)) {
                return ResponseEntity.status(200).body("이미 거부된 사용자입니다.");
            }
        }
        Long admin_id = userStudyService.getOwnerbyStudyId(studyId);
        String jwt = testService.getJWTByUserID(admin_id);

        joinRequestService.acceptJoinRequest(studyId, userId, jwt);
        return ResponseEntity.status(200).body("쇼터디에 가입했어요."); //신청 수락 성공
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

    @GetMapping("/test/localDataTime")
    private LocalDateTime printServerTime(){
        LocalDateTime now = LocalDateTime.now();
        return now;
    }


    @Data
    @AllArgsConstructor
    public static class Result<T>{
        private T data;
        private String message;
    }
}