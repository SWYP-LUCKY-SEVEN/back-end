package com.example.swip.dto.study;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudyUpdateResponse {
    private String title;
    private String description;
    private List<String> tags;
}
