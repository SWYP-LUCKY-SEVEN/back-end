package com.example.swip.dto.quick_match;

import com.example.swip.entity.Category;
import com.example.swip.entity.SavedQuickMatchFilter;
import com.example.swip.entity.enumtype.Tendency;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class QuickMatchFilter {
    private String category;
    private LocalDateTime start_date;
    private String duration;
    private String tendency;
    private List<Long> mem_scope;
    //DTO 들어온 뒤, Study, StudyCategory, Category, AddicionalInfo, UserStudy 에 정보 저장해야함.
    public SavedQuickMatchFilter toQuickFilterEntity(Long userId, Category findCategory) {
        return SavedQuickMatchFilter.builder()
                .id(userId)
                .start_date(this.start_date)
                .duration(this.duration)
                .mem_scope(this.mem_scope) //생성시 작성자 1명 참여하므로 1
                .tendency(toTendency(this.tendency))
                .category(findCategory)
                .build();
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
}
