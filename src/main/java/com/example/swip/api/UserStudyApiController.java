package com.example.swip.api;

import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.userStudy.UserStudyResponse;
import com.example.swip.entity.User;
import com.example.swip.entity.UserStudy;
import com.example.swip.service.UserStudyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserStudyApiController {
    private final UserStudyService userStudyService;

    @Operation(summary = "스터디별 참여 멤버 조회 (방장용) - 참여 멤버 중 방장을 제외한 나머지만 조회됨")
    @GetMapping("/study/{study_id}/user")
    private ResponseEntity<Result> GetAllUsersByStudyId(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("study_id") Long studyId
    ){
        Long ownerId = userPrincipal.getUserId();
        Long findStudyOwner = userStudyService.getOwnerbyStudyId(studyId);
        if(!ownerId.equals(findStudyOwner)) {
            return ResponseEntity.status(403).body(new Result(null, "방장이 아닙니다."));
        }
        List<UserStudy> allUsersByStudyId = userStudyService.getAllNotExitedUsersByStudyId(studyId);
        List<UserStudyResponse> responses = allUsersByStudyId.stream()
                .map(userStudy -> {
                    User user = userStudy.getUser();
                    return UserStudyResponse.builder()
                            .study_id(userStudy.getId().getStudyId())
                            .user_id(userStudy.getId().getUserId())
                            .join_date(userStudy.getJoin_date())
                            .nickname(user.getNickname())
                            .profile_image(user.getProfile_image())
                            .build();
                }).collect(Collectors.toList());

        return ResponseEntity.status(200).body(new Result(responses, "조회 성공"));
    }

    @Operation(summary = "스터디별 참여 멤버 내보내기 (방장용)")
    @PostMapping("/study/{study_id}/user/out")
    private ResponseEntity<String> GetStudyMembetOut(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("study_id") Long studyId,
            @RequestParam Long userId,
            @RequestParam List<String> exitReasons //내보내기 사유
    ){
        Long ownerId = userPrincipal.getUserId();
        Long findStudyOwner = userStudyService.getOwnerbyStudyId(studyId);
        if(!ownerId.equals(findStudyOwner)) {
            return ResponseEntity.status(403).body("방장이 아닙니다.");
        }
        //TODO: 방장은 내보낼 수 없도록하는 로직 추가.
        else {
            // TODO: 이미 내보낸 멤버는 내보내지 못하도록 하는 로직 추가.
            userStudyService.getMemberOutOfStudy(studyId, userId, exitReasons);
            return ResponseEntity.status(200).body("스터디 멤버 내보내기 성공");
        }
    }

    @Data
    @AllArgsConstructor
    public static class Result<T>{
        private T data;
        private String message;
    }
}
