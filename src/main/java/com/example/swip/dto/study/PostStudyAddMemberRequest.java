package com.example.swip.dto.study;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostStudyAddMemberRequest {
    private String token;
    private Long studyId;
    private Long userId;
    private int type;
}
