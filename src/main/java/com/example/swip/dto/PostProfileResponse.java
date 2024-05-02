package com.example.swip.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostProfileResponse {
    private String nickname;
    private String profileImgLink;
}
