package com.example.swip.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetNicknameDupleResponse {
    private boolean isDuplicate;
}
