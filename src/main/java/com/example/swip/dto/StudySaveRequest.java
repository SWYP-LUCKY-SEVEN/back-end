package com.example.swip.dto;

import com.example.swip.entity.Category;
import com.example.swip.entity.Study;
import com.example.swip.entity.User;
import com.example.swip.entity.UserStudy;
import com.example.swip.entity.compositeKey.UserStudyId;
import com.example.swip.entity.enumtype.ExitStatus;
import com.example.swip.entity.enumtype.MatchingType;
import com.example.swip.entity.enumtype.StudyProgressStatus;
import com.example.swip.entity.enumtype.Tendency;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudySaveRequest {
    private List<String> categories;  //카테고리들
    private String title;
    private String description;
    private List<String> tags; //태그 - additional info 들
    private LocalDateTime start_date;
    private String duration;
    private int max_participants_num;
    private String matching_type;
    private String tendency;

    private Long writerId; //작성자 ID


    //DTO 들어온 뒤, Study, StudyCategory, Category, AddicionalInfo, UserStudy 에 정보 저장해야함.
    public Study toStudyEntity() {
        return Study.builder()
                .title(this.title)
                .description(this.description)
                .start_date(this.start_date)
                .end_date(toEndDate(this.start_date, this.duration))
                .max_participants_num(this.max_participants_num)
                .cur_participants_num(1) //생성시 작성자 1명 참여하므로 1
                .tendency(toTendency(this.tendency))
                .matching_type(toMatchingType(this.matching_type))
                .status(toStatus())
                .recruit_status(true) //모집중
                .build();
    }

    //end date 계산하는 메서드
    private LocalDateTime toEndDate(LocalDateTime start_date, String duration){
        LocalDateTime end_date = null;
        // end_date = start_date + duration
        if(duration.equals("일주일")){
            end_date = start_date.plusWeeks(1);
        } else if (duration.equals("한 달")) {
            end_date = start_date.plusMonths(1);
        } else if (duration.equals("3개월")) {
            end_date = start_date.plusMonths(3);
        } else if (duration.equals("6개월")) {
            end_date = start_date.plusMonths(6);
        }
        // duration == 상시, 미정 -> end_date = null
        return end_date;
    }
    // tendency 검사
    private Tendency toTendency(String tendency){
        Tendency result = null;
        if(tendency.equals("활발한 대화와 동기부여 원해요")){
            result = Tendency.Active;
        } else if (tendency.equals("학습 피드백을 주고 받고 싶어요")) {
            result = Tendency.Feedback;
        } else if (tendency.equals("조용히 집중하고 싶어요")) {
            result = Tendency.Focus;
        }
        return result;
    }
    // matching type 검사
    private MatchingType toMatchingType(String matching_type){
        MatchingType result = null;
        if(matching_type.equals("승인제")){
            result = MatchingType.Approval;
        } else if (matching_type.equals("빠른 매칭")) {
            result = MatchingType.Quick;
        }
        return result;
    }
    // progress_status 검사 => client에서 enddate가 오늘보다 이전인 스터디 생성하면 안됨.
    private StudyProgressStatus toStatus(){
        LocalDateTime today = LocalDateTime.now();
        StudyProgressStatus result = StudyProgressStatus.BeforeStart;
        if (today.isBefore(start_date)){
            result = StudyProgressStatus.BeforeStart;
        }
        else{
            result = StudyProgressStatus.InProgress;
        }
        return result;
    }
}