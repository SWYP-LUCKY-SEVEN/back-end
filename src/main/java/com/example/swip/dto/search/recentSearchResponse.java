package com.example.swip.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class recentSearchResponse {
    private String keyword;
}
