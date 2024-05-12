package com.example.swip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserEvaluationRequest {
    List<EvaluationRequest> eval_list;
}
