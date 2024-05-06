package com.example.swip.repository;

import com.example.swip.entity.SavedQuickMatchFilter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuickFilterRepository extends JpaRepository<SavedQuickMatchFilter, Long>{
}
