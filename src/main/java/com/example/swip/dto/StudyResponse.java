package com.example.swip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class StudyResponse {
    private Long id;
    private String title;
    private LocalDateTime start_date; //시작 날짜
    private LocalDateTime end_date; //종료 날짜
    private int max_participants_num;
    private int cur_participants_num;
    private String category;
    private List<String> additionalInfos = new ArrayList<>(); //태그

}
