package com.example.swip.repository;

import com.example.swip.entity.QuickFilter;
import com.example.swip.entity.S3AccessTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuickFilterRepository extends JpaRepository<QuickFilter, Long> {
}
