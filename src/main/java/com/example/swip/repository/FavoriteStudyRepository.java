package com.example.swip.repository;

import com.example.swip.entity.FavoriteStudy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteStudyRepository extends JpaRepository<FavoriteStudy, Long> {
}
