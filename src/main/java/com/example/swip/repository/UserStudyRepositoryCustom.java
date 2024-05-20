package com.example.swip.repository;

import com.example.swip.entity.Study;
import com.example.swip.entity.UserStudy;

import java.util.List;

public interface UserStudyRepositoryCustom {
    Long findOwnerByStudyId(Long studyId);

    List<UserStudy> findStudyByUserId(Long userId);

    List<UserStudy> findStudyByStudyIdExceptOwner(Long studyId);

    List<UserStudy> findAllNotExitedUsersBySyudyId(Long studyId);

    List<UserStudy> findAllByStudyId(Long studyId);
}
