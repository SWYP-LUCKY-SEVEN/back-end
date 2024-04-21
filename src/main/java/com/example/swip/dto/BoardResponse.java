package com.example.swip.dto;

import com.example.swip.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BoardResponse {
    private Long id;
    private String title;
    private Long writerId;
}
