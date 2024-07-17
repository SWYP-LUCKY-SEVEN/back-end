package com.example.swip.repository.custom;

import com.example.swip.entity.Study;
import com.example.swip.entity.UserStudy;
import com.example.swip.entity.compositeKey.UserStudyId;
import com.example.swip.entity.enumtype.ChatStatus;

import java.util.List;

public interface UserStudyRepositoryCustom {
    Long findOwnerByStudyId(Long studyId);

    List<UserStudy> findStudyByUserId(Long userId);

    List<UserStudy> findStudyByStudyIdExceptOwner(Long studyId);

    List<UserStudy> findAllNotExitedUsersBySyudyId(Long studyId);

    List<UserStudy> findAllByStudyId(Long studyId);

    UserStudy findUserStudyById(UserStudyId userStudyId);
}
