package com.example.swip.repository;

import com.example.swip.entity.UserStudy;
import com.example.swip.entity.compositeKey.UserStudyId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStudyRepository extends JpaRepository<UserStudy, UserStudyId> {
}
