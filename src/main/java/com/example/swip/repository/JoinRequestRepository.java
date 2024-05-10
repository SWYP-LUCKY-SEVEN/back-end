package com.example.swip.repository;

import com.example.swip.entity.JoinRequest;
import com.example.swip.entity.compositeKey.JoinRequestId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, JoinRequestId>, JoinRequestRepositoryCustom {
}
