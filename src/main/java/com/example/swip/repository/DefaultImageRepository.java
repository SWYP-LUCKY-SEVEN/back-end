package com.example.swip.repository;

import com.example.swip.entity.DefaultImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DefaultImageRepository extends JpaRepository<DefaultImage, Long> {
    List<DefaultImage> findAllByType(String type);
}
