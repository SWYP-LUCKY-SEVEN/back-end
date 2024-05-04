package com.example.swip.repository;

import com.example.swip.dto.StudyFilterCondition;
import com.example.swip.dto.StudyFilterResponse;

import java.util.List;

public interface StudyFilterRepository {
    List<StudyFilterResponse> filterStudy(StudyFilterCondition filterCondition);
}
