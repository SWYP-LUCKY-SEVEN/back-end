package com.example.swip.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class deletedSearchResponse {
    private boolean deleteStatus; //삭제 여부
    private long deleteCount; //삭제된 개수
}
