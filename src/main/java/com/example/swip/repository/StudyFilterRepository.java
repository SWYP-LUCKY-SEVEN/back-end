package com.example.swip.repository;

import com.example.swip.dto.quick_match.QuickMatchFilter;
import com.example.swip.dto.quick_match.QuickMatchResponse;
import com.example.swip.dto.study.StudyFilterCondition;
import com.example.swip.dto.study.StudyFilterResponse;
import com.example.swip.entity.Study;
import com.querydsl.core.Tuple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface StudyFilterRepository {
    List<StudyFilterResponse> filterStudy(StudyFilterCondition filterCondition);
    List<QuickMatchResponse> quickFilterStudy(QuickMatchFilter quickMatchFilter, Long userId, Long page, Long size);
    List<Study> progressStartStudy(LocalDate date);
    List<Study> completeExpiredStudy(LocalDate date);

}
