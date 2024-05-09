package com.example.swip.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class popularSearchReponse {
    private Long searchId;
    private String keyword;
    private Long totalCount;
}
