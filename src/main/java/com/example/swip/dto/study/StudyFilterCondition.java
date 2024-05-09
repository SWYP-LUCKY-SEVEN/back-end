package com.example.swip.dto.study;

import lombok.*;

import java.time.LocalDate;
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

    private String category;
    private LocalDate start_date;
    private String duration; //또는 end_date
    private Integer min_participants;
    private Integer max_participants;
    private List<String> tendency;

    private String order_type;

}
