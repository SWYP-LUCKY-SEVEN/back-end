package com.example.swip.api;

import com.example.swip.config.security.UserPrincipal;
import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.JoinRequest.JoinRequestResponse;
import com.example.swip.entity.enumtype.JoinStatus;
import com.example.swip.service.JoinRequestService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JoinRequestApiController {
    private final JoinRequestService joinRequestService;

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
        Long loginUserId = userPrincipal.getUserId();

        //방장이면 확인 가능하도록
        if(joinRequestService.isStudyOwner(studyId, loginUserId)){
            List<JoinRequestResponse> responses= joinRequestService.getAllByStudyId(studyId);
            //dto 변환 & return
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
        Long loginUserId = userPrincipal.getUserId();
        //방장이면 확인 가능하도록
        if(userPrincipal == null){
            return ResponseEntity.status(401).body(new Result(null,"로그인이 필요합니다."));
        }
        if(joinRequestService.isStudyOwner(studyId, loginUserId)){
            Integer size = joinRequestService.getAllWaitingCountByStudyId(studyId);
            return ResponseEntity.status(200).body(new Result(size, "성공"));
        }
        return ResponseEntity.status(403).body(new Result(null, "방장이 아닙니다."));
    }

    @Operation(summary = "신청 수락 (방장용)")
    @PostMapping("/joinRequest/accept")
    private ResponseEntity<DefaultResponse> acceptJoinRequest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam Long studyId,
            @RequestParam Long userId
    )
    {
        if(userPrincipal == null){
            return ResponseEntity.status(401).body(new DefaultResponse("로그인이 필요합니다."));
        }
        Long loginUserId = userPrincipal.getUserId();
        if(!joinRequestService.isStudyOwner(studyId, loginUserId)) {
            return ResponseEntity.status(403).body(new DefaultResponse("방장이 아닙니다."));
        }
        //꽉찬 스터디의 경우 수락 불가
        boolean isFull = joinRequestService.isAlreadyFull(studyId);
        if(isFull){
            return ResponseEntity.status(200).body(new DefaultResponse("참여 인원이 꽉 찼습니다."));
        }
        //이미 신청 수락/거부한 경우(더블체크)
        JoinStatus joinStatus = joinRequestService.checkJoinStatusById(studyId, userId);
        if(joinStatus != null) {
            if (joinStatus.equals(JoinStatus.Approved)) {
                return ResponseEntity.status(200).body(new DefaultResponse("이미 수락된 사용자입니다."));
            } else if (joinStatus.equals(JoinStatus.Rejected)) {
                return ResponseEntity.status(200).body(new DefaultResponse("이미 거부된 사용자입니다."));
            }
        }

        joinRequestService.acceptJoinRequest(studyId, userId, userPrincipal.getToken());
        return ResponseEntity.status(200).body(new DefaultResponse("쇼터디에 가입했어요.")); //신청 수락 성공
    }

    @Operation(summary = "신청 거부 (방장용)")
    @PostMapping("/joinRequest/reject")
    private ResponseEntity<DefaultResponse> rejectJoinRequest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam Long studyId,
            @RequestParam Long userId
    )
    {
        if(userPrincipal == null){
            return ResponseEntity.status(401).body(new DefaultResponse("로그인이 필요합니다."));
        }
        Long loginUserId = userPrincipal.getUserId();
        if(!joinRequestService.isStudyOwner(studyId, loginUserId)) {
            return ResponseEntity.status(403).body(new DefaultResponse("방장이 아닙니다."));
        }
        //이미 신청 수락/거부한 경우(더블체크)
        JoinStatus joinStatus = joinRequestService.checkJoinStatusById(studyId, userId);
        if(joinStatus != null) {
            if (joinStatus.equals(JoinStatus.Approved)) {
                return ResponseEntity.status(200).body(new DefaultResponse("이미 수락된 사용자입니다."));
            } else if (joinStatus.equals(JoinStatus.Rejected)) {
                return ResponseEntity.status(200).body(new DefaultResponse("이미 거부된 사용자입니다."));
            }
        }

        joinRequestService.rejectJoinRequest(studyId, userId);
        return ResponseEntity.status(200).body(new DefaultResponse("신청 거부 성공"));
    }

    @Operation(summary = "나의 스터디 - 신청 취소 API")
    @PostMapping("/joinRequest/cancel")
    private ResponseEntity<DefaultResponse> cancelWaitingStudy(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam Long studyId
    ) {
        if (userPrincipal != null) {
            Long userId = userPrincipal.getUserId();
            boolean cancleStatus = joinRequestService.cancelJoinRequest(userPrincipal.getToken(), userId, studyId);
            if (cancleStatus) {
                return ResponseEntity.status(200).body(new DefaultResponse("스터디 취소 성공!"));
            } else {
                return ResponseEntity.status(403).body(new DefaultResponse("해당 스터디 신청 내역이 없거나 취소할 수 없는 사용자입니다."));
            }
        }
        return ResponseEntity.status(403).body(new DefaultResponse("인증되지 않은 사용자입니다."));
    }

    // List 값을 Result로 한 번 감싸서 return하기 위한 class
    @Data
    @AllArgsConstructor
    public static class Result<T>{
        private T data;
        private String message;
    }

}
