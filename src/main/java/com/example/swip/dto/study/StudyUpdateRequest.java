package com.example.swip.dto.study;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StudyUpdateRequest {

    private String title;
    private String description;
    private List<String> tags;

}

