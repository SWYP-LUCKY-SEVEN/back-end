package com.example.swip.dto.quick_match;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QuickMatchResponse {
    private List<QuickMatchStudy> data;
    private Boolean hasNextPage;
    private Integer totalCount;
}
