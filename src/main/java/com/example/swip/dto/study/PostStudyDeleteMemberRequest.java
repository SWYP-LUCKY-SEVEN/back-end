package com.example.swip.dto.study;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostStudyDeleteMemberRequest {
    private String token;
    private String studyId;
    private String userId;
}
