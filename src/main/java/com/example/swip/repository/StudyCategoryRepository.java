package com.example.swip.repository;

import com.example.swip.entity.StudyCategory;
import com.example.swip.entity.compositeKey.StudyCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCategoryRepository extends JpaRepository<StudyCategory, StudyCategoryId> {
}
