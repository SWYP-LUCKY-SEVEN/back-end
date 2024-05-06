package com.example.swip.dto.quick_match;

import com.example.swip.entity.Category;
import com.example.swip.entity.SavedQuickMatchFilter;
import com.example.swip.entity.enumtype.Tendency;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class QuickMatchFilter {
    private String category;
    private LocalDate start_date;
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
                .tendency(Tendency.toTendency(this.tendency))
                .category(findCategory)
                .build();
    }
}
