package com.example.swip.dto.study;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostStudyRequest {
    private Long studyId;
    private Long pk;
    private String name;
}
