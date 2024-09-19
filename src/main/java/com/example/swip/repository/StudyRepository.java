package com.example.swip.repository;

import com.example.swip.entity.Study;
import com.example.swip.entity.enumtype.ChatStatus;
import com.example.swip.repository.custom.StudyRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long>, StudyFilterRepository, StudyRepositoryCustom {
    ChatStatus findChat_statusById(Long Id);
}
