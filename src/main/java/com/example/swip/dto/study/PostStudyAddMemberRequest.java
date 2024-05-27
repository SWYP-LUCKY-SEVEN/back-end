package com.example.swip.dto.study;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostStudyAddMemberRequest {
    private Long studyId;
    private Long userId;
    private String type;
}
