package com.example.swip.dto.chat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteStudyRequest {
    private String groupId;
    private String token;
}
