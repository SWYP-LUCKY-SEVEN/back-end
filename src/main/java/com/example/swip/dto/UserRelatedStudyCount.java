package com.example.swip.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class UserRelatedStudyCount {
    private Long in_progress; // 진행중
    private Long in_proposal; // 참가 신청
    private Long in_favorite; // 즐겨찾기
    private Long in_complete; // 완료
}
