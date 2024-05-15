package com.example.swip.repository;

import com.example.swip.entity.FavoriteStudy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteStudyRepository extends JpaRepository<FavoriteStudy, Long> {
    FavoriteStudy findByUserIdAndStudyId(Long user_id, Long study_id);
}
