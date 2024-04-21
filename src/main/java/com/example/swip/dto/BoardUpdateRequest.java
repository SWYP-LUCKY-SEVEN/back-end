package com.example.swip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardUpdateRequest {

    private String title;
    private String content;

}

