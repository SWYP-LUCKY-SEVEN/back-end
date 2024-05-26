package com.example.swip.dto.user;

import com.example.swip.dto.chat.ChatProfileRequest;
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

    public ChatProfileRequest toChatUserProfileDto() {
        return ChatProfileRequest.builder()
                .pk(this.user_id.toString())
                .nickname(this.nickname)
                .pic(this.profileImage)
                .build();
    }
}
