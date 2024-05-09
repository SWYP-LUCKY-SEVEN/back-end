package com.example.swip.repository;

import com.example.swip.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchRepository extends JpaRepository<Search, Long> {
    boolean existsByKeyword(String queryString);

    Search findByKeyword(String queryString);

    @Query("SELECT s FROM Search s ORDER BY s.count DESC LIMIT 20") // default: 최근 검색순 정렬.
    List<Search> findTop20ByCount();
}
