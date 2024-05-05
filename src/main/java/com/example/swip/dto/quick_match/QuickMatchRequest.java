package com.example.swip.dto.quick_match;

import com.example.swip.config.UserPrincipal;
import com.example.swip.entity.Category;
import com.example.swip.entity.SavedQuickMatchFilter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuickMatchRequest {
    private String category;
    private LocalDateTime startDate;
    private String duration;
    private String tendency;
    private List<Long> mem_scope;

}
