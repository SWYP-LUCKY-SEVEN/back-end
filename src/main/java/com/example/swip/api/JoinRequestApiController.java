package com.example.swip.api;

import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.JoinRequest.JoinRequestResponse;
import com.example.swip.entity.JoinRequest;
import com.example.swip.entity.enumtype.JoinStatus;
import com.example.swip.service.JoinRequestService;
import com.example.swip.service.StudyService;
import com.example.swip.service.UserStudyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class JoinRequestApiController {
    private final JoinRequestService joinRequestService;
    private final UserStudyService userStudyService;
    private final StudyService studyService;

    @Operation(summary = "스터디 신청 내역 조회 (방장용)")
    @GetMapping("/joinRequest/{study_id}")
    public ResponseEntity<Result> getJoinRequestsByStudyId(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("study_id") Long studyId
            )
    {
        if(userPrincipal == null){
            return ResponseEntity.status(401).body(new Result<>(null, "로그인이 필요합니다."));
        }
        Long ownerId = userPrincipal.getUserId();
        Long findStudyOwner = userStudyService.getOwnerbyStudyId(studyId);

        //방장이면 확인 가능하도록
        if(ownerId.equals(findStudyOwner)){
            List<JoinRequest> findJoinRequests= joinRequestService.getAllByStudyId(studyId);
            //dto 변환 & return
            List<JoinRequestResponse> responses = findJoinRequests.stream()
                    .map(request -> {
                        return JoinRequestResponse.builder()
                                .study_id(request.getId().getStudyId())
                                .user_id(request.getId().getUserId())
                                .join_status(request.getJoin_status().toString())
                                .request_date(request.getRequest_date())
                                .nickname(request.getUser().getNickname())
                                .profile_image(request.getUser().getProfile_image())
                                .build();
                    }).collect(Collectors.toList());
            return ResponseEntity.status(200).body(new Result(responses, "성공"));
        }
        return ResponseEntity.status(403).body(new Result(null, "방장이 아닙니다."));
    }

    @Operation(summary = "스터디 승인 대기중 인원수 조회(방장용)")
    @GetMapping("/joinRequest/count/{study_id}")
    public ResponseEntity<Result> getJoinRequestCountByStudyId(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("study_id") Long studyId
    ){
        Long ownerId = userPrincipal.getUserId();
        Long findStudyOwner = userStudyService.getOwnerbyStudyId(studyId);
        //방장이면 확인 가능하도록
        if(userPrincipal == null){
            return ResponseEntity.status(401).body(new Result(null,"로그인이 필요합니다."));
        }
        if(ownerId.equals(findStudyOwner)){
            Integer size = joinRequestService.getAllWaitingCountByStudyId(studyId);
            return ResponseEntity.status(200).body(new Result(size, "성공"));
        }
        return ResponseEntity.status(403).body(new Result(null, "방장이 아닙니다."));
    }

    @Operation(summary = "신청 수락 (방장용)")
    @PostMapping("/joinRequest/accept")
    private ResponseEntity<String> acceptJoinRequest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam Long studyId,
            @RequestParam Long userId
    )
    {
        if(userPrincipal == null){
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        Long ownerId = userPrincipal.getUserId();
        Long findStudyOwner = userStudyService.getOwnerbyStudyId(studyId);
        if(!ownerId.equals(findStudyOwner)) {
            return ResponseEntity.status(403).body("방장이 아닙니다.");
        }
        //꽉찬 스터디의 경우 수락 불가
        boolean isFull = studyService.isAlreadyFull(studyId);
        if(isFull){
            return ResponseEntity.status(202).body("참여 인원이 꽉 찼습니다.");
        }
        //이미 신청 수락/거부한 경우(더블체크)
        JoinStatus joinStatus = joinRequestService.checkJoinStatusById(studyId, userId);
        if(joinStatus != null) {
            if (joinStatus.equals(JoinStatus.Approved)) {
                return ResponseEntity.status(202).body("이미 수락된 사용자입니다.");
            } else if (joinStatus.equals(JoinStatus.Rejected)) {
                return ResponseEntity.status(202).body("이미 거부된 사용자입니다.");
            }
        }

        joinRequestService.acceptJoinRequest(studyId, userId, userPrincipal.getToken());
        return ResponseEntity.status(200).body("신청 수락 성공");
    }

    @Operation(summary = "신청 거부 (방장용)")
    @PostMapping("/joinRequest/reject")
    private ResponseEntity<String> rejectJoinRequest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam Long studyId,
            @RequestParam Long userId
    )
    {
        if(userPrincipal == null){
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        Long ownerId = userPrincipal.getUserId();
        Long findStudyOwner = userStudyService.getOwnerbyStudyId(studyId);
        if(!ownerId.equals(findStudyOwner)) {
            return ResponseEntity.status(403).body("방장이 아닙니다.");
        }
        //이미 신청 수락/거부한 경우(더블체크)
        JoinStatus joinStatus = joinRequestService.checkJoinStatusById(studyId, userId);
        if(joinStatus != null) {
            if (joinStatus.equals(JoinStatus.Approved)) {
                return ResponseEntity.status(202).body("이미 수락된 사용자입니다.");
            } else if (joinStatus.equals(JoinStatus.Rejected)) {
                return ResponseEntity.status(202).body("이미 거부된 사용자입니다.");
            }
        }

        joinRequestService.rejectJoinRequest(studyId, userId);
        return ResponseEntity.status(200).body("신청 거부 성공");
    }

    @Operation(summary = "나의 스터디 - 신청 취소 API")
    @PostMapping("/joinRequest/cancel")
    private ResponseEntity<String> cancelWaitingStudy(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam Long studyId
    ) {
        if (userPrincipal != null) {
            Long userId = userPrincipal.getUserId();
            boolean cancleStatus = joinRequestService.cancelJoinRequest(userId, studyId);
            if (cancleStatus) {
                return ResponseEntity.status(200).body("스터디 취소 성공!");
            } else {
                return ResponseEntity.status(403).body("해당 스터디 신청 내역이 없습니다.");
            }
        }
        return ResponseEntity.status(403).body("인증되지 않은 사용자입니다.");
    }

    // List 값을 Result로 한 번 감싸서 return하기 위한 class
    @Data
    @AllArgsConstructor
    public static class Result<T>{
        private T data;
        private String message;
    }

}
