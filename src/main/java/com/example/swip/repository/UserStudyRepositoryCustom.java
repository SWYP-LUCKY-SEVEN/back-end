package com.example.swip.repository;

import com.example.swip.entity.Study;
import com.example.swip.entity.UserStudy;

import java.util.List;

public interface UserStudyRepositoryCustom {
    List<UserStudy> findAllExistUsersByStudyId(Long studyId);

    Long findOwnerByStudyId(Long studyId);

    List<UserStudy> findStudyByUserId(Long userId);

    List<UserStudy> findAllNotExitedUsersBySyudyId(Long studyId);
}
