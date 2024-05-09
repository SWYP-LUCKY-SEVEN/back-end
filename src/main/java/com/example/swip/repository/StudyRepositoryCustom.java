package com.example.swip.repository;

import com.example.swip.entity.Study;
import com.querydsl.core.Tuple;

import java.util.List;

public interface StudyRepositoryCustom {

    List<Study> findStudyDetailById(Long id);
}
