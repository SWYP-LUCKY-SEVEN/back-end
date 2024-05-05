package com.example.swip.dto.quick_match;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class QuickMatchResponse {
    private Long study_id;
    private String title;
    private String category;
    private LocalDateTime start_date; //시작 날짜
    private String duration; //종료 날짜
    private int max_participants_num;
    private int cur_participants_num;
    private LocalDateTime created_time;

    @QueryProjection
    public QuickMatchResponse(Long study_id, String title, String category, LocalDateTime start_date, String duration, int max_participants_num, int cur_participants_num, LocalDateTime created_time) {
        this.study_id = study_id;
        this.title = title;
        this.category = category;
        this.start_date = start_date;
        this.duration = duration;
        this.max_participants_num = max_participants_num;
        this.cur_participants_num = cur_participants_num;
        this.created_time = created_time;
    }
}
