package com.example.swip.repository;

import com.example.swip.entity.JoinRequest;

import java.util.List;

public interface JoinRequestRepositoryCustom {
    List<JoinRequest> findAllByStudyId(Long studyId);

    List<JoinRequest> findAllWaitingByStudyId(Long studyId);
}