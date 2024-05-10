package com.example.swip.dto.quick_match;

import com.example.swip.entity.enumtype.Tendency;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class QuickMatchResponse {
    private Long study_id;
    private String title;
    private String category;
    private LocalDate start_date; //시작 날짜
    private String duration; //종료 날짜
    private int max_participants_num;
    private int cur_participants_num;
    private LocalDateTime created_time;
    private Tendency.Element tendency; //종료 날짜

    @QueryProjection
    public QuickMatchResponse(Long study_id, String title, String category, LocalDate start_date, String duration, int max_participants_num, int cur_participants_num, LocalDateTime created_time, Tendency.Element tendency) {
        this.study_id = study_id;
        this.title = title;
        this.category = category;
        this.start_date = start_date;
        this.duration = duration;
        this.max_participants_num = max_participants_num;
        this.cur_participants_num = cur_participants_num;
        this.created_time = created_time;
        this.tendency = tendency;
    }
}
