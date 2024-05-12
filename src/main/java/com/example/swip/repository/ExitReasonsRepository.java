package com.example.swip.repository;

import com.example.swip.entity.ExitReasons;
import com.example.swip.entity.enumtype.ExitReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExitReasonsRepository extends JpaRepository<ExitReasons, Long> {
    ExitReasons findByReason(ExitReason.Element er);
}
