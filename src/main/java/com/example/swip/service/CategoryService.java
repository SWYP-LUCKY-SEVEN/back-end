package com.example.swip.service;

import com.example.swip.dto.StudySaveRequest;
import com.example.swip.entity.Category;
import com.example.swip.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category findCategoryIdByName(String categoryName) {
        Category category = categoryRepository.findByName(categoryName);
        return category;
    }
}
