package com.example.swip.repository;

import com.example.swip.entity.User;
import com.querydsl.core.Tuple;

import java.util.List;

public interface UserStudyRepositoryCustom {
    List<Tuple> findAllUsersByStudyId(Long studyId);
}
