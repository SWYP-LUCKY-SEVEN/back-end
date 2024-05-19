package com.example.swip.repository;

import com.example.swip.entity.JoinRequest;
import com.example.swip.entity.compositeKey.JoinRequestId;
import com.example.swip.entity.enumtype.JoinStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface JoinRequestRepositoryCustom {
    List<JoinRequest> findAllByStudyId(Long studyId);

    List<JoinRequest> findAllWaitingByStudyId(Long studyId);

    void deleteExpiredJoinRequest(LocalDateTime time);

    JoinStatus findJoinStatusById(JoinRequestId joinRequestId);
}
