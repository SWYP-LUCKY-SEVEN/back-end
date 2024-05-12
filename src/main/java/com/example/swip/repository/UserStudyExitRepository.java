package com.example.swip.repository;

import com.example.swip.entity.UserStudyExit;
import com.example.swip.entity.compositeKey.UserStudyExitId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStudyExitRepository extends JpaRepository<UserStudyExit, UserStudyExitId> {
}
