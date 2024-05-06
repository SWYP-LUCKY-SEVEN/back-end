package com.example.swip.dto;

import com.example.swip.entity.Category;
import com.example.swip.entity.Study;
import com.example.swip.entity.enumtype.MatchingType;
import com.example.swip.entity.enumtype.StudyProgressStatus;
import com.example.swip.entity.enumtype.Tendency;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudySaveRequest {
    private String category;  //카테고리
    private String title;
    private String description;
    private List<String> tags; //태그 - additional info 들
    private String start_date; // yyyy-MM-dd 형식
    private String duration;
    private int max_participants_num;
    private String matching_type;
    private String tendency;


    //DTO 들어온 뒤, Study, StudyCategory, Category, AddicionalInfo, UserStudy 에 정보 저장해야함.
    public Study toStudyEntity(Category findCategory) {
        return Study.builder()
                .title(this.title)
                .description(this.description)
                .start_date(toLocalDate(this.start_date))
                .end_date(toEndDate(toLocalDate(this.start_date), this.duration))
                .duration(this.duration)
                .max_participants_num(this.max_participants_num)
                .cur_participants_num(1) //생성시 작성자 1명 참여하므로 1
                .tendency(toTendency(this.tendency))
                .matching_type(toMatchingType(this.matching_type))
                .status(toStatus())
                .recruit_status(true) //모집중
                .category(findCategory)
                .build();
    }

    // String을 LocalDate로 변환하는 메서드
    private LocalDate toLocalDate(String dateString) {
        return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
    }

    //end date 계산하는 메서드
    private LocalDate toEndDate(LocalDate start_date, String duration){
        LocalDate end_date = null;
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
        LocalDate today = LocalDate.now();
        StudyProgressStatus result = StudyProgressStatus.BeforeStart;
        if (today.isBefore(toLocalDate(start_date))){
            result = StudyProgressStatus.BeforeStart;
        }
        else{
            result = StudyProgressStatus.InProgress;
        }
        return result;
    }
}
