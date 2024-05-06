package com.example.swip.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class StudyFilterCondition {
    //빠른 매칭 제외
    //=========================================//
    //분야 - 전체, 수능, 대학생, ... 11개
    //기간 - 시작 날짜, 기간
    //인원 - min, max
    //타입 - 3가지
    // + 정렬 순서 : 최근 등록순(default), 인기순, 마감 임박순, 가나다순
    private String page_type; //신규, 전체, 마감임박, 승인없음

    private String query_string;

    private String quick_match; //빠른매칭: true, 승인제:false

    private List<String> categories;
    private LocalDateTime start_date;
    private String duration; //또는 end_date
    private Integer max_participants;
    private String tendency;

    private String order_type;

    //빠른 매칭 - 다음에도 조건 기억할래요.
    //private boolean remember;

}
