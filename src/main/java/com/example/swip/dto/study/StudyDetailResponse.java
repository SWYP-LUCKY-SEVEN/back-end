package com.example.swip.dto.study;

import com.example.swip.dto.UserRelationship;
import com.example.swip.entity.enumtype.MatchingType;
import com.example.swip.entity.enumtype.StudyProgressStatus;
import com.example.swip.entity.enumtype.Tendency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class StudyDetailResponse {
    private String title;
    private String description;
    private List<String> tags;
    private String category;
    private String matching_type;

    private LocalDate start_date; //시작 날짜
    private LocalDate end_date; //종료 날짜
    private String duration;

    private int max_participants_num;
    private int cur_participants_num;

    private String tendency; //스터디 성향 - enum?

    //참여 멤버
    private List<StudyDetailMembers> membersList;

    private UserRelationship userRelation;
}
