package com.example.swip.api;

import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.*;
import com.example.swip.dto.auth.GetNicknameDupleResponse;
import com.example.swip.dto.user.PostProfileDto;
import com.example.swip.dto.user.PostProfileRequest;
import com.example.swip.dto.user.PostProfileResponse;
import com.example.swip.dto.study.StudyFilterResponse;
import com.example.swip.dto.user.UserEvaluationRequest;
import com.example.swip.dto.user.UserMainProfileDto;
import com.example.swip.dto.user.UserProfileGetResponse;
import com.example.swip.dto.user.UserRelatedStudyCount;
import com.example.swip.dto.userStudy.UserProgressStudyResponse;
import com.example.swip.entity.User;
import com.example.swip.service.*;
import com.mysema.commons.lang.Pair;
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
    private final UserWithdrawalService userWithdrawalService;

    @Operation(summary = "닉네임 중복 확인", description = "path param으로 입력된 nickname의 존재 여부를 반환함.")
    @GetMapping("/user/nickname/{nickname}") //
    public ResponseEntity<GetNicknameDupleResponse> NicknameDuplicateCheck(
            @PathVariable("nickname") String nickname
    ) {
        return ResponseEntity.status(200).body(GetNicknameDupleResponse.builder()
                .isDuplicate(userService.isDuplicatedNickname(nickname))
                .build());
    }

    @Operation(summary = "회원가입 시 프로필 등록", description = "회원가입 시 프로필을 등록하는 메소드입니다. 헤더 내 Authorization:Bearer ~ 형태의 JWT 토큰을 필요로 합니다.")
    @PostMapping("/user/profile") // swagger를 위해 변형을 줌
    public ResponseEntity<DefaultResponse> postUserProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Validated PostProfileRequest postProfileRequest
    ) {
        if(principal == null)
            return ResponseEntity.status(403).build();

        PostProfileDto postProfileDto = postProfileRequest.toPostProfileDto(principal.getUserId());
        boolean check = userService.updateProfile(postProfileDto);
        if (!check)
            return ResponseEntity.status(400).build();

        Pair<String, Integer> response = chatServerService.postUser(postProfileDto);

        return ResponseEntity.status(200).body(DefaultResponse.builder()
                .message("chat server response : "+response.getFirst() + response.getSecond().toString())
                .build());
    }

    @Operation(summary = "프로필 수정", description = "회원가입 시 프로필을 수정하는 메소드입니다. 헤더 내 Authorization:Bearer ~ 형태의 JWT 토큰을 필요로 합니다.")
    @PatchMapping("/user/profile") // swagger를 위해 변형을 줌
    public ResponseEntity<DefaultResponse> patchUserProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Validated PostProfileRequest postProfileRequest
    ) {
        if(principal == null)
            return ResponseEntity.status(403).build();

        PostProfileDto postProfileDto = postProfileRequest.toPostProfileDto(principal.getUserId());
        boolean check = userService.updateProfile(postProfileDto);
        if (!check)
            return ResponseEntity.status(400).build();

        Pair<String, Integer> response = chatServerService.updateUser(postProfileDto);

        return ResponseEntity.status(200).body(DefaultResponse.builder()
                .message("chat server response : "+response.getFirst() + response.getSecond().toString())
                .build());
    }

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
    @Operation(summary = "진행중인 스터디 목록 확인",
            description = "")
    @GetMapping("/user/progress/study")
    public ResponseEntity getProgressStudy(
            @AuthenticationPrincipal UserPrincipal userPrincipal // 권한 인증
    ) {
        if(userPrincipal == null)
            return ResponseEntity.status(403).body(
                    DefaultResponse.builder()
                            .message("로그인이 필요합니다.")
                            .build());

        List<UserProgressStudyResponse> userProgressStudy = null;
        userProgressStudy = studyService.getProgressStudyList(userPrincipal.getUserId());
        int totalCount = userProgressStudy.size(); //전체 리스트 개수

        return ResponseEntity.status(200).body(
                new StudyApiController.Result(userProgressStudy,totalCount)
        );
    }

    @Operation(summary = "참가 스터디 목록 확인",
            description = "status (null 허용): before, progress, done.\n" +
                    "- null일 경우: 완료 되지 않은 참여 중인 스터디 출력. (before + progress 와 동일)\n" +
                    "- before : 시작 전인 스터디 출력\n" +
                    "- progress : 진행 중인 스터디 출력\n" +
                    "- done : 완료한 스터디 출력")
    @GetMapping("/user/registered/study")
    public ResponseEntity getRegisteredStudy(
            @AuthenticationPrincipal UserPrincipal userPrincipal, // 권한 인증
            @RequestParam(required = false) String status
    ) {
        if(userPrincipal == null)
            return ResponseEntity.status(403).body(
                    DefaultResponse.builder()
                            .message("로그인이 필요합니다.")
                            .build());

        List<StudyFilterResponse> filteredStudy = null;
        filteredStudy = studyService.getRegisteredStudyList(userPrincipal.getUserId(), status);
        int totalCount = filteredStudy.size(); //전체 리스트 개수

        return ResponseEntity.status(200).body(
                new StudyApiController.Result(filteredStudy,totalCount)
        );
    }
    @Operation(summary = "회원 평점 확인")
    @GetMapping("/user/{nickname}/rating") //
    public Integer getRatingUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("nickname") String nickname
    ) {
        return userService.getUserRatingByNickname(nickname);
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
        if(userPrincipal.getUserId() == evaluationRequest.getTo_id())
            return ResponseEntity.status(403).body(
                    DefaultResponse.builder()
                            .message("본인 평가는 불가능합니다.")
                            .build());
        boolean check = userService.evaluationUser(evaluationRequest.getTo_id(),
                userPrincipal.getUserId(),
                evaluationRequest.getScore().intValue());

        if(!check)
            return ResponseEntity.status(400).body(
                    DefaultResponse.builder()
                            .message("score 값이 범위를 벗어났습니다. 0 부터 100 까지")
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
        List<EvaluationRequest> evaluationList = evaluationRequest.getEval_list();
        boolean check = true;
        for(EvaluationRequest evaluation : evaluationList) {
            check = userService.evaluationUser(evaluation.getTo_id(),
                    userPrincipal.getUserId(),
                    evaluation.getScore().intValue());
        }
        if(!check)
            return ResponseEntity.status(200).body(
                    DefaultResponse.builder()
                            .message("범위를 벗어난 score 값, 혹은 본인을 평가한 요청을 제외하고 적용되었습니다.")
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
            return ResponseEntity.status(401).build();

        Pair<Integer, Long> result = userWithdrawalService.withdrawal(principal.getUserId(), false);

        if(result.getFirst() != 201) {
            String massage = result.getSecond() == null ? "등록되지 않은 JWT" : "운영중인 스터디가 있습니다.";
            return ResponseEntity.status(result.getFirst()).body(
                    DefaultResponse.builder()
                            .message(massage)
                            .build()
            );
        }
        Pair<String, Integer> response = chatServerService.deleteUser(result.getSecond());

        return ResponseEntity.status(result.getFirst()).body(DefaultResponse.builder()
                        .message("chat server response : "+response.getFirst() + response.getSecond().toString())
                .build());
    }
}