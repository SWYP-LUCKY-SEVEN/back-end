package com.example.swip.api;

import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.*;
import com.example.swip.dto.auth.GetNicknameDupleResponse;
import com.example.swip.dto.auth.PostProfileDto;
import com.example.swip.dto.auth.PostProfileRequest;
import com.example.swip.dto.auth.PostProfileResponse;
import com.example.swip.dto.study.StudyFilterResponse;
import com.example.swip.entity.Evaluation;
import com.example.swip.entity.User;
import com.example.swip.service.ChatServerService;
import com.example.swip.service.FavoriteStudyService;
import com.example.swip.service.StudyService;
import com.example.swip.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserApiController {
    private final FavoriteStudyService favoriteStudyService;
    private final ChatServerService chatServerService;
    private final UserService userService;
    private final StudyService studyService;

    @Operation(summary = "공유 프로필 정보 반환", description = "마이프로필 외 위치에서 사용자 프로필을 조회할 때, 사용됩니다.")
    @GetMapping("/user/profile") // swagger를 위해 변형을 줌
    public ResponseEntity<UserProfileGetResponse> getUserProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String nickname
    ){
        if(principal == null)
            return ResponseEntity.status(403).build();

        UserMainProfileDto profile = userService.getMainProfileByNickname(nickname);
        if(profile == null)
            return ResponseEntity.status(404).body(
                    UserProfileGetResponse.builder()
                            .massage("사용자를 찾을 수 없습니다.")
                            .build());

        UserRelatedStudyCount ursCount = userService.getPublicRelatedStudyNum(profile.getUser_id());

        return ResponseEntity.status(200).body(
                UserProfileGetResponse.builder()
                        .profile(profile)
                        .study_count(ursCount)
                        .massage("Success!")
                        .build()
        );
    }

    @Operation(summary = "닉네임 중복 확인", description = "path param으로 입력된 nickname의 존재 여부를 반환함.")
    @GetMapping("/user/nickname/{nickname}") //
    public ResponseEntity<GetNicknameDupleResponse> NicknameDuplicateCheck(
            @PathVariable("nickname") String nickname
    ) {
        return ResponseEntity.status(200).body(GetNicknameDupleResponse.builder()
                .isDuplicate(userService.isDuplicatedNickname(nickname))
                .build());
    }

    @Operation(summary = "회원가입 시 프로필 생성 메소드", description = "회원가입 시 프로필을 생성하는 메소드입니다. 헤더 내 Authorization:Bearer ~ 형태의 JWT 토큰을 필요로 합니다. " +
            "우선 회원정보 변경시에도 해당 API를 사용 가능합니다. 회원 정보 변경은 Chat 서버의 고려사항을 파악 후 완성하려 합니다.")
    @PatchMapping("/user/profile") // swagger를 위해 변형을 줌
    public ResponseEntity<PostProfileResponse> postUserProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Validated PostProfileRequest postProfileRequest
    ) {
        if(principal == null)
            return ResponseEntity.status(403).build();

        PostProfileDto postProfileDto = postProfileRequest.toPostProfileDto(principal.getUserId());
        boolean check = userService.updateProfile(postProfileDto);
        if (!check)
            return ResponseEntity.status(400).build();

        PostProfileResponse postProfileResponse = chatServerService.postUser(postProfileDto);

        return ResponseEntity.status(201).body(postProfileResponse);
    }


    @Operation(summary = "마이프로필 정보 반환 (JWT 필요)", description = "마이프로필에서 사용자 정보를 확인할 때 사용됩니다. 자신의 프로필을 받아옵니다.")
    @GetMapping("/user/profile/me") // swagger를 위해 변형을 줌
    public ResponseEntity<UserProfileGetResponse> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal
    ){
        if(principal == null)
            return ResponseEntity.status(403).build();

        UserMainProfileDto profile = userService.getMainProfileById(principal.getUserId());
        if(profile == null)
            return ResponseEntity.status(404).body(
                    UserProfileGetResponse.builder()
                            .massage("사용자를 찾을 수 없습니다.")
                            .build());

        UserRelatedStudyCount ursCount = userService.getRelatedStudyNum(principal.getUserId());

        return ResponseEntity.status(200).body(
                UserProfileGetResponse.builder()
                        .profile(profile)
                        .study_count(ursCount)
                        .massage("Success!")
                        .build()
        );
    }

    @Operation(summary = "내 찜 목록 확인",
            description = "스터디 찜 리스트 확인")
    @GetMapping("/user/favorite/study")
    public ResponseEntity getFavoriteStudy(
            @AuthenticationPrincipal UserPrincipal userPrincipal // 권한 인증
    ) {
        if(userPrincipal == null)
            return ResponseEntity.status(403).body(
                    DefaultResponse.builder()
                            .message("로그인이 필요합니다.")
                            .build());


        List<StudyFilterResponse> filteredStudy =
                favoriteStudyService.getFavoriteStudyList(userPrincipal.getUserId());
        int totalCount = filteredStudy.size(); //전체 리스트 개수

        return ResponseEntity.status(200).body(
                new StudyApiController.Result(filteredStudy,totalCount)
        );
    }


    @Operation(summary = "내 스터디 신청 목록 확인",
            description = "내 스터디 신청 목록 확인")
    @GetMapping("/user/proposer/study")
    public ResponseEntity getProposerStudy(
            @AuthenticationPrincipal UserPrincipal userPrincipal // 권한 인증
    ) {
        if(userPrincipal == null)
            return ResponseEntity.status(403).body(
                    DefaultResponse.builder()
                            .message("로그인이 필요합니다.")
                            .build());
        List<StudyFilterResponse> filteredStudy =
                studyService.getProposerStudyList(userPrincipal.getUserId());
        int totalCount = filteredStudy.size(); //전체 리스트 개수

        return ResponseEntity.status(200).body(
                new StudyApiController.Result(filteredStudy,totalCount)
        );
    }

    @Operation(summary = "참가 스터디 목록 확인",
            description = "before, progress, done")
    @GetMapping("/user/registered/study")
    public ResponseEntity getRegisteredStudy(
            @AuthenticationPrincipal UserPrincipal userPrincipal, // 권한 인증
            @RequestParam String status
    ) {
        if(userPrincipal == null)
            return ResponseEntity.status(403).body(
                    DefaultResponse.builder()
                            .message("로그인이 필요합니다.")
                            .build());

        List<StudyFilterResponse> filteredStudy =
                studyService.getRegisteredStudyList(userPrincipal.getUserId(), status);
        int totalCount = filteredStudy.size(); //전체 리스트 개수

        return ResponseEntity.status(200).body(
                new StudyApiController.Result(filteredStudy,totalCount)
        );
    }
    @Operation(summary = "회원 평점 확인")
    @GetMapping("/user/{user_id}/rating") //
    public int getRatingUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("user_id") Long userId
    ) {
        return userService.getUserRating(userId);
    }
    @Operation(summary = "회원 평가 진행", description = "score 값은 0~100까지 입니다.")
    @PostMapping("/user/evaluation") //
    public ResponseEntity<DefaultResponse> postEvaluationUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody EvaluationRequest evaluationRequest
    ) {
        if(userPrincipal == null)
            return ResponseEntity.status(403).body(
                    DefaultResponse.builder()
                            .message("로그인이 필요합니다.")
                            .build());
        boolean check = userService.evaluationUser(evaluationRequest.getTo_id(),
                userPrincipal.getUserId(),
                evaluationRequest.getScore().intValue());

        if(!check)
            return ResponseEntity.status(200).body(
                    DefaultResponse.builder()
                            .message("score 값이 범위를 벗어났습니다.")
                            .build());

        return ResponseEntity.status(201).body(
                DefaultResponse.builder()
                        .message("평가가 성공적으로 저장되었습니다.")
                        .build());
    }
    @Operation(summary = "회원 평가 진행 (단체)", description = "score 값은 0~100까지 입니다.")
    @PostMapping("/user/evaluation/list") //
    public ResponseEntity<DefaultResponse> postEvaluationUserList(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody UserEvaluationRequest evaluationRequest
    ) {
        if(userPrincipal == null)
            return ResponseEntity.status(403).body(
                    DefaultResponse.builder()
                            .message("로그인이 필요합니다.")
                            .build());
        User user = userService.findUserById(userPrincipal.getUserId());
        List<EvaluationRequest> evaluationList = evaluationRequest.getEval_list();
        boolean check = true;
        for(EvaluationRequest evaluation : evaluationList) {
            check = userService.evaluationUser(evaluation.getTo_id(),
                    user,
                    evaluation.getScore().intValue());
        }
        if(!check)
            return ResponseEntity.status(200).body(
                    DefaultResponse.builder()
                            .message("score 값이 범위를 벗어났습니다.")
                            .build());

        return ResponseEntity.status(201).body(
                DefaultResponse.builder()
                        .message("평가 리스트가 성공적으로 저장되었습니다.")
                        .build());
    }
  
    @Operation(summary = "회원 탈퇴", description = "JWT 토큰 해당하는 계정에 탈퇴 과정을 진행합니다.")
    @PatchMapping("/user/withdrawal") //
    public ResponseEntity<DefaultResponse> withdrawalUser(@AuthenticationPrincipal UserPrincipal principal) {
        if(principal == null)
            return null;
        Long userId = userService.withdrawal(principal.getUserId());
        if(userId==null)
            return ResponseEntity.status(404).build();

        return ResponseEntity.status(200).body(
                chatServerService.deleteUser(userId)
        );
    }
}
