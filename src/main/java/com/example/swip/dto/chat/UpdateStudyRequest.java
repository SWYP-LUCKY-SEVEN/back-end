package com.example.swip.dto.chat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateStudyRequest {
    private String chatId;
    private String chatName;
    private String token;
}
