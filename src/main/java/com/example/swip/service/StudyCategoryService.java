package com.example.swip.service;

import com.example.swip.entity.Category;
import com.example.swip.entity.Study;
import com.example.swip.entity.StudyCategory;
import com.example.swip.entity.compositeKey.StudyCategoryId;
import com.example.swip.repository.StudyCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyCategoryService {

    private final StudyCategoryRepository studyCategoryRepository;

    @Transactional
    public void saveStudyCategory(List<Category> findCategories, Study study){
        for (Category findCategory : findCategories) {
            StudyCategoryId id = new StudyCategoryId(study.getId(), findCategory.getId());

            StudyCategory studyCategory = StudyCategory
                    .builder()
                    .id(id)
                    .study(study)
                    .category(findCategory)
                    .build();

            studyCategoryRepository.save(studyCategory);
        }
    }
}
