package com.example.swip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EvaluationRequest {
    private Long to_id;
    private Long score;
}
