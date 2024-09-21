package com.example.swip.service;

import com.example.swip.dto.quick_match.QuickMatchFilter;
import com.example.swip.dto.quick_match.QuickMatchResponse;
import com.example.swip.dto.quick_match.QuickMatchStudy;
import com.example.swip.entity.Category;
import com.example.swip.entity.SavedQuickMatchFilter;
import com.example.swip.entity.User;
import com.example.swip.entity.enumtype.Tendency;
import com.example.swip.repository.QuickFilterRepository;
import com.example.swip.repository.StudyRepository;
import com.example.swip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyQuickService {

    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final QuickFilterRepository quickFilterRepository;

    private final CategoryService categoryService;

    @Transactional
    public QuickMatchFilter getQuickMatchFilter(Long userId) {
        SavedQuickMatchFilter savedFilter = quickFilterRepository.findById(userId).orElse(null);
        if(savedFilter == null)
            return null;
        return QuickMatchFilter.builder()
                .category(savedFilter.getCategory().getName())
                .duration(savedFilter.getDuration())
                .mem_scope(
                        QuickMatchFilter.longToMemScope(savedFilter.getMem_scope())
                )
                .tendency(Tendency.longToString(savedFilter.getTendency()))
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
        User user = userRepository.findById(userId).orElse(null); //작성자 정보 조회
        if(user == null)
            return 0L;

        //category id 조회
        Category findCategory = categoryService.findCategoryIdByName(quickMatchFilter.getCategory());

        SavedQuickMatchFilter savedFilter = null;
        if(quickFilterRepository.existsById(userId)) {
            savedFilter = quickFilterRepository.findById(userId).orElse(null);
            if (savedFilter != null)
                savedFilter.updateFilter(
                        findCategory,
                        quickMatchFilter.getDuration(),
                        Tendency.stringToLong(quickMatchFilter.getTendency()),
                        QuickMatchFilter.memScopeToLong(quickMatchFilter.getMem_scope())
                );
        }else {
            savedFilter = quickFilterRepository.save(
                    quickMatchFilter.toQuickFilterEntity(user, findCategory)
            );
        }

        //return
        return savedFilter.getId();
    }
    public QuickMatchResponse quickFilteredStudy(QuickMatchFilter quickMatchFilter, Long userId, Long page, Long size){
        return studyRepository.quickFilterStudy(quickMatchFilter, userId, page, size);
    }
}
