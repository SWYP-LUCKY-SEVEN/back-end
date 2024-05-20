package com.example.swip.dto.userStudy;

import com.example.swip.dto.todo.MemberTodoResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserProgressStudyResponse {
    private Long id;
    private String title;
    private String category;
    private String status;
    private LocalDate start_date; //시작 날짜
    private LocalDate end_date; //종료 날짜
    private int max_participants_num;
    private int cur_participants_num;
    private MemberTodoResponse progress_todo;

    @QueryProjection
    public UserProgressStudyResponse(Long id, String title, String category, String status, LocalDate start_date, LocalDate end_date, int max_participants_num, int cur_participants_num, MemberTodoResponse progress_todo) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.status = status;
        this.start_date = start_date;
        this.end_date = end_date;
        this.max_participants_num = max_participants_num;
        this.cur_participants_num = cur_participants_num;
        this.progress_todo = progress_todo;
    }
}
