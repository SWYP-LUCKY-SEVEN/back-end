package com.example.swip.api;

import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.PostProfileResponse;
import com.example.swip.entity.User;
import com.example.swip.service.S3Service;
import com.example.swip.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ProfileApiController {

    private final S3Service s3Service;
    private final UserService userService;
    /**
     * 생성
     */
    @Operation(summary = "회원가입 시 프로필 생성 메소드", description = "회원가입 시 프로필을 생성하는 메소드입니다. req 형식은 multipart/form-data 입니다. 입력은 JWT 계정과 알맞은 userID가 반드시 URI에 포함되어야 하며, 아닐경우 null을 반환합니다. 헤더 내 Authorization:Bearer ~ 형태의 JWT 토큰을 필요로 합니다.")
    @PostMapping(value="/profile/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // swagger를 위해 변형을 줌
    public PostProfileResponse create(
            @PathVariable("id") Long userId,
            @RequestPart(value = "nickname") String nickname,
            @RequestPart(value = "profileImg", required = false) MultipartFile multipartFile
    ) {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user == null)
            return null;
        if(user.getUserId() != userId) {
            System.out.println("your ID is"+user.getUserId());
            return null;    //response 헤더에서 에러처리 필요.
        }
        System.out.println(user.getEmail());
        String imageLink = s3Service.putS3Test(multipartFile);

        userService.updateProfile(userId, imageLink, nickname);
        return null;
    }
}
