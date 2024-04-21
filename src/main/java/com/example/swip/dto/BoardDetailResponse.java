package com.example.swip.dto;

import com.example.swip.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class BoardDetailResponse {
    private Long id;
    private String title;
    private String content;
    private Long writerId;
    private LocalDateTime created_time;
    private LocalDateTime updated_time;
}
