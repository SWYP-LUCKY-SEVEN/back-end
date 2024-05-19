package com.example.swip.repository;

import com.example.swip.entity.JoinRequest;
import com.example.swip.entity.compositeKey.JoinRequestId;
import com.example.swip.entity.enumtype.JoinStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, JoinRequestId>, JoinRequestRepositoryCustom {
}
