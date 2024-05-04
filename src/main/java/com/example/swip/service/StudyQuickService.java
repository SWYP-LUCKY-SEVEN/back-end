package com.example.swip.service;

import com.example.swip.dto.QuickMatchDto;
import com.example.swip.entity.Category;
import com.example.swip.entity.QuickFilter;
import com.example.swip.entity.User;
import com.example.swip.repository.QuickFilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyQuickService {

    private final QuickFilterRepository quickFilterRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    @Transactional
    public Long saveQuickMatchFilter(QuickMatchDto quickMatchDto, Long userId){
        //작성자 정보 조회
        User user = userService.findUserById(userId); //작성자 정보 조회
        if(user == null)
            return 0L;

        //category id 조회
        Category findCategory = categoryService.findCategoryIdByName(quickMatchDto.getCategory());

        //study 저장
        QuickFilter savedFilter = quickFilterRepository.save(
                quickMatchDto.toQuickFilterEntity(user.getId(), findCategory)
        );

        //return
        return savedFilter.getId();
    }

}
