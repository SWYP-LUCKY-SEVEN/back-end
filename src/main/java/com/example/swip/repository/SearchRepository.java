package com.example.swip.repository;

import com.example.swip.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchRepository extends JpaRepository<Search, Long> {
    boolean existsByKeyword(String searchString);

    Search findByKeyword(String searchString);

    @Query("SELECT s FROM Search s WHERE s.count > 4 ORDER BY s.count DESC LIMIT 6") // default: 최근 검색순 정렬.
    List<Search> findTop6ByCount();
}
