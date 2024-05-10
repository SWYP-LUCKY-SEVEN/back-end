package com.example.swip.api;

import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.UserMainProfileDto;
import com.example.swip.dto.UserProfileGetResponse;
import com.example.swip.dto.UserRelatedStudyCount;
import com.example.swip.dto.auth.GetNicknameDupleResponse;
import com.example.swip.dto.auth.PostProfileDto;
import com.example.swip.dto.auth.PostProfileRequest;
import com.example.swip.dto.auth.PostProfileResponse;
import com.example.swip.service.ChatServerService;
import com.example.swip.service.FavoriteStudyService;
import com.example.swip.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserApiController {
    private final FavoriteStudyService favoriteStudyService;
    private final ChatServerService chatServerService;
    private final UserService userService;

    @Operation(summary = "프로필 정보 반환", description = ".")
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
    @Operation(summary = "프로필 정보 반환", description = ".")
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

    @Operation(summary = "닉네임 중복 확인", description = "path param으로 입력된 nickname의 존재 여부를 반환함.")
    @GetMapping("/user/nickname/{nickname}") //
    public ResponseEntity<GetNicknameDupleResponse> NicknameDuplicateCheck(
            @PathVariable("nickname") String nickname
    ) {
        return ResponseEntity.status(200).body(GetNicknameDupleResponse.builder()
                .isDuplicate(userService.isDuplicatedNickname(nickname))
                .build());
    }
}
