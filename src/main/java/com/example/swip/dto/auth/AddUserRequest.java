package com.example.swip.dto.auth;

import com.example.swip.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddUserRequest {
    private String email;
    private String nickname;

    public User toEntity() {
        return User.builder()
                .email(this.email)
                .nickname(this.nickname)
                .role("USER")
                .build();
    }
    public User toTestEntity() {
        return User.builder()
                .join_date(LocalDateTime.now())
                .email(this.email)
                .nickname(this.nickname)
                .role("USER")
                .validate("test")
                .profile_image("https://res.cloudinary.com/dsfyp40dr/image/upload/v1714733931/p1zlj1jdedvvglagq9qw.png")
                .build();
    }
}