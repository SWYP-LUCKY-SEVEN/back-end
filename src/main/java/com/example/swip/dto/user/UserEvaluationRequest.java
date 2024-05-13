package com.example.swip.dto.user;

import com.example.swip.dto.EvaluationRequest;
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
