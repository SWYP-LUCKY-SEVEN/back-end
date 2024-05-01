package com.example.swip.repository;

import com.example.swip.entity.S3AccessTime;
import org.springframework.data.jpa.repository.JpaRepository;


public interface S3AccessTimeRepository extends JpaRepository<S3AccessTime, Long> {
    S3AccessTime findByStrYearMonth(String yearMonth);
}
