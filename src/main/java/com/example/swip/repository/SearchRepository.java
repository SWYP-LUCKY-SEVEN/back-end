package com.example.swip.repository;

import com.example.swip.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRepository extends JpaRepository<Search, Long> {
    boolean existsByKeyword(String queryString);

    Search findByKeyword(String queryString);
}
