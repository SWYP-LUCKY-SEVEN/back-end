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

    // 카테고리 조회

    //이름 list에 해당하는 모든 category 조회
    public List<Category> findCategoryIdsMyName(List<String> categoryNames){
        List<Category> categories = new ArrayList<>();
        for (String categoryName : categoryNames) {
            Category findCategory = categoryRepository.findByName(categoryName);
            if(findCategory != null){
                categories.add(findCategory);
            }
        }
        return categories;
    }

    public Category findCategoryIdByName(String categoryName) {
        Category category = categoryRepository.findByName(categoryName);
        return category;
    }
}
