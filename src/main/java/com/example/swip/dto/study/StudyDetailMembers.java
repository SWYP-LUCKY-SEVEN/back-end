package com.example.swip.dto.study;

import com.example.swip.entity.enumtype.ExitStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyDetailMembers {
    private String nickname;
    private String profileImage;
    private boolean is_owner;
    private ExitStatus exit_status;
}
