package com.example.swip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserProfileGetResponse {
    private UserMainProfileDto profile;
    private UserRelatedStudyCount study_count;
    private String massage;
}
