package com.example.swip.dto.JoinRequest;

import com.example.swip.entity.enumtype.JoinStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class JoinRequestResponse {
    private Long study_id;
    private Long user_id;
    private LocalDateTime request_date;
    private String join_status;
    private String nickname;
    private String profile_image;
}
