package com.example.swip.dto.quick_match;

import com.example.swip.dto.UserRelationship;
import com.example.swip.entity.enumtype.Tendency;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class QuickMatchStudy {
    private Long study_id;
    private String match_type;
    private String title;
    private String category;
    private String description;
    private LocalDate start_date; //시작 날짜
    private String duration;
    private int max_participants_num;
    private int cur_participants_num;
    private LocalDateTime created_time;
    private Tendency.Element tendency;
    List<String> additional_infos = new ArrayList<>();
    private UserRelationship user_relation;

    @QueryProjection
    public QuickMatchStudy(Long study_id, String match_type, String title, String category,
                           String description, LocalDate start_date,
                           String duration, int max_participants_num,
                           int cur_participants_num, LocalDateTime created_time,
                           Tendency.Element tendency, List<String> additional_infos,
                           UserRelationship user_relation) {
        this.study_id = study_id;
        this.match_type = match_type;
        this.title = title;
        this.category = category;
        this.description = description;
        this.start_date = start_date;
        this.duration = duration;
        this.max_participants_num = max_participants_num;
        this.cur_participants_num = cur_participants_num;
        this.created_time = created_time;
        this.tendency = tendency;
        this.additional_infos = additional_infos;
        this.user_relation = user_relation;
    }

}
