package com.example.swip.dto.study;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostStudyRequest {
    private String studyId;
    private String pk;
    private String name;
}
