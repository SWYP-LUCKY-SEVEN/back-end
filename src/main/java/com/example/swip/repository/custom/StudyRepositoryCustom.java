package com.example.swip.repository.custom;

import com.example.swip.entity.Study;
import com.querydsl.core.Tuple;

import java.util.List;

public interface StudyRepositoryCustom {

    Study findStudyDetailById(Long id);
    Study findStudyEditDetailById(Long id);
}
