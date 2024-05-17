package com.example.swip.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleUserProfileDto {
    private Long user_id;
    private String nickname;
    private String profileImage;
    private boolean is_owner;
}
