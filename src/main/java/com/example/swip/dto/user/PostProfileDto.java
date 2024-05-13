package com.example.swip.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostProfileDto {
    private Long user_id;
    private String nickname;
    private String profileImage;
}
