package com.example.swip.dto.study;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyDetailMembers {
    private String nickname;
    private boolean is_owner;
}
