package com.example.swip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class StudyDetailResponse {
    private Long id;
    private String title;
    private String content;
    private Long writerId;
    private LocalDateTime created_time;
    private LocalDateTime updated_time;
}
