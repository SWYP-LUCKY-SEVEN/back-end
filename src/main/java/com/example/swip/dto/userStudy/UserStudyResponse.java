package com.example.swip.dto.userStudy;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserStudyResponse {
    private Long study_id;
    private Long user_id;
    private LocalDateTime join_date;
    private String nickname;
    private String profile_image;
}
