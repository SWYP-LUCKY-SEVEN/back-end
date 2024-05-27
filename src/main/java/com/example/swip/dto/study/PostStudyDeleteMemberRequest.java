package com.example.swip.dto.study;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostStudyDeleteMemberRequest {
    private Long studyId;
    private Long userId;
}
