package com.example.swip.dto.study;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyFilterResponse {

    private Long id;
    private String title;
    private String status;
    private LocalDate start_date; //시작 날짜
    private LocalDate end_date; //종료 날짜
    private int max_participants_num;
    private int cur_participants_num;
    private LocalDateTime created_time;
    private String category;
    private List<String> additionalInfos = new ArrayList<>(); //태그
    private Boolean is_member;

    @QueryProjection
    public StudyFilterResponse(Long id, String title, String status, LocalDate start_date, LocalDate end_date, int max_participants_num, int cur_participants_num, LocalDateTime created_time, String category, List<String> additionalInfos, Boolean is_member) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.start_date = start_date;
        this.end_date = end_date;
        this.max_participants_num = max_participants_num;
        this.cur_participants_num = cur_participants_num;
        this.created_time = created_time;
        this.category = category;
        this.additionalInfos = additionalInfos;
        this.is_member = is_member;
    }
    @QueryProjection
    public StudyFilterResponse(Long id, String title, String status, LocalDate start_date, LocalDate end_date, int max_participants_num, int cur_participants_num, LocalDateTime created_time, String category, List<String> additionalInfos) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.start_date = start_date;
        this.end_date = end_date;
        this.max_participants_num = max_participants_num;
        this.cur_participants_num = cur_participants_num;
        this.created_time = created_time;
        this.category = category;
        this.additionalInfos = additionalInfos;
    }

    @Getter
    public static class AdditionalInfoDto {
        private String name;

        public AdditionalInfoDto(String name) {
            this.name = name;
        }
    }
}
