package com.example.swip.dto.quick_match;

import com.example.swip.entity.Category;
import com.example.swip.entity.SavedQuickMatchFilter;
import com.example.swip.entity.User;
import com.example.swip.entity.enumtype.Tendency;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuickMatchFilter {
    private String page_type; //신규, 전체, 마감임박, 승인없음
    private String query_string; // 검색 관련인듯함.
    private String quick_match; //빠른매칭: quick, 승인제:false

    private String order_type;

    private String category;
    private LocalDate start_date;
    private String duration;
    private List<String> tendency;
    private List<Long> mem_scope;
    //DTO 들어온 뒤, Study, StudyCategory, Category, AddicionalInfo, UserStudy 에 정보 저장해야함.
    public SavedQuickMatchFilter toQuickFilterEntity(User user, Category findCategory) {
        return SavedQuickMatchFilter.builder()
                .start_date(this.start_date)
                .duration(this.duration)
                .mem_scope(memScopeToLong(this.mem_scope))
                .tendency(Tendency.stringToLong(tendency))
                .category(findCategory)
                .user(user)
                .build();
    }
    public static Long memScopeToLong(List<Long> mem_scope){
        Long scopeLong = 0L;
        for (Long value : mem_scope) {
            Long addValue = 1L;
            for (Long i = 0L; i < 4; i++) {
                if (value == i) {
                    scopeLong += addValue;
                    break;
                }
                addValue = addValue << 1;
            }
        }
        return scopeLong;
    }
    public static List<Long> longToMemScope(Long scopeLong){
        List<Long> mem_scope = new ArrayList<>();
        for (Long i = 0L; i < 4; i++) {
            if(scopeLong == 0)
                break;
            if(scopeLong % 2 == 1)
                mem_scope.add(i);
            scopeLong = scopeLong >> 1;
        }
        return mem_scope;
    }
}
