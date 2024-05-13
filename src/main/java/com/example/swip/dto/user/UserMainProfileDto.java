package com.example.swip.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserMainProfileDto {
    private String nickname;
    private String profile_img;
    private String email;
    private Long user_id;
    private int rating;
}
