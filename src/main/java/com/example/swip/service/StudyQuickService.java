package com.example.swip.service;

import com.example.swip.dto.quick_match.QuickMatchFilter;
import com.example.swip.dto.quick_match.QuickMatchResponse;
import com.example.swip.entity.Category;
import com.example.swip.entity.SavedQuickMatchFilter;
import com.example.swip.entity.User;
import com.example.swip.entity.enumtype.Tendency;
import com.example.swip.repository.QuickFilterRepository;
import com.example.swip.repository.StudyFilterRepository;
import com.example.swip.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyQuickService {

    private final StudyRepository studyRepository;
    private final QuickFilterRepository quickFilterRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    @Transactional
    public QuickMatchFilter getQuickMatchFilter(Long userId) {
        SavedQuickMatchFilter savedFilter = quickFilterRepository.findById(userId).orElse(null);
        return QuickMatchFilter.builder()
                .category(savedFilter.getCategory().getName())
                .start_date(savedFilter.getStart_date())
                .duration(savedFilter.getDuration())
                .min_member(savedFilter.getMin_member())
                .max_member(savedFilter.getMax_member())
                .tendency(Tendency.toString(savedFilter.getTendency()))
                .build();
    }
    @Transactional
    public void deleteQuickMatchFilter(Long userId) {
        if(quickFilterRepository.existsById(userId))
            quickFilterRepository.deleteById(userId);
    }
    @Transactional
    public Long saveQuickMatchFilter(QuickMatchFilter quickMatchFilter, Long userId){
        //작성자 정보 조회
        User user = userService.findUserById(userId); //작성자 정보 조회
        if(user == null)
            return 0L;

        //category id 조회
        Category findCategory = categoryService.findCategoryIdByName(quickMatchFilter.getCategory());

        //filter 저장
        SavedQuickMatchFilter savedFilter = quickFilterRepository.save(
                quickMatchFilter.toQuickFilterEntity(user.getId(), findCategory)
        );

        //return
        return savedFilter.getId();
    }
    public List<QuickMatchResponse> quickFilteredStudy(QuickMatchFilter quickMatchFilter, Long page, Long size){
        List<QuickMatchResponse> FilteredStudyList = studyRepository.quickFilterStudy(quickMatchFilter, page, size);
        return FilteredStudyList;
    }
}
