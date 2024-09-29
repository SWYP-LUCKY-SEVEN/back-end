package com.example.swip.dto.oauth;

import com.example.swip.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoRegisterDto {
    private String email;
    private String nickname;
    private String role;

    public User toEntity(String profile_image) {
        return User.builder()
                .email(this.email)
                .nickname(this.nickname)
                .profile_image(profile_image)
                .role(this.role)
                .validate("kakao")
                .build();
    }
}
