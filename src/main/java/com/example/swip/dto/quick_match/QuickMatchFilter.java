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
    private String page_type; //신규, 전체, 마감임박, 승인없음
    private String query_string; // 검색 관련인듯함.
    private String quick_match; //빠른매칭: quick, 승인제:false

    private String order_type;

    private String category;
    private LocalDate start_date;
    private String duration;
    private List<String> tendency;
    private Long min_member;
    private Long max_member;
    //DTO 들어온 뒤, Study, StudyCategory, Category, AddicionalInfo, UserStudy 에 정보 저장해야함.
    public SavedQuickMatchFilter toQuickFilterEntity(Long userId, Category findCategory) {
        return SavedQuickMatchFilter.builder()
                .id(userId)
                .start_date(this.start_date)
                .duration(this.duration)
                .min_member(this.min_member)
                .max_member(this.max_member)
                .tendency(Tendency.stringToLong(tendency))
                .category(findCategory)
                .build();
    }
}
