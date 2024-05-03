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

    public User toEntity() {
        return User.builder()
                .email(this.email)
                .nickname(this.nickname)
                .role(this.role)
                .validate("kakao")
                .build();
    }
}
