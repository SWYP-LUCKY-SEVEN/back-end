package com.example.swip.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostProfileRequest {
    private String nickname;
    private String profileImage;
    public PostProfileDto toPostProfileDto(Long user_id) {
        return PostProfileDto.builder()
                .user_id(user_id)
                .nickname(this.nickname)
                .profileImage(this.profileImage)
                .build();
    }
}
