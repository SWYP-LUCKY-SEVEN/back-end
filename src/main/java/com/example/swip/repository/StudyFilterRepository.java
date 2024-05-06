package com.example.swip.repository;

import com.example.swip.dto.quick_match.QuickMatchFilter;
import com.example.swip.dto.quick_match.QuickMatchResponse;
import com.example.swip.dto.StudyFilterCondition;
import com.example.swip.dto.StudyFilterResponse;
import com.example.swip.entity.Study;

import java.util.List;

public interface StudyFilterRepository {
    List<StudyFilterResponse> filterStudy(StudyFilterCondition filterCondition);
    List<QuickMatchResponse> quickFilterStudy(QuickMatchFilter quickMatchFilter, Long page);
}
