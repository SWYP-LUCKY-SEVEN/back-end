package com.example.swip.service;

import com.example.swip.dto.*;
import com.example.swip.entity.Category;
import com.example.swip.entity.Study;
import com.example.swip.entity.User;
import com.example.swip.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final AdditionalInfoService additionalInfoService;
    private final StudyCategoryService studyCategoryService;
    private final UserStudyService userStudyService;


    //저장
    @Transactional
    public Long saveStudy(StudySaveRequest studySaveRequest){
        //작성자 정보 조회
        User writer = userService.findUserById(studySaveRequest.getWriterId()); //작성자 정보 조회

        //category ids 조회
        List<Category> findCategories = categoryService.findCategoryIdsMyName(studySaveRequest.getCategories());

        //study 저장
        Study savedStudy = studyRepository.save(studySaveRequest.toStudyEntity());

        //study_category 저장
        studyCategoryService.saveStudyCategory(findCategories, savedStudy);

        //additional_info 저장
        additionalInfoService.saveAddInfo(studySaveRequest.getTags(), savedStudy);

        //user_study - 방장 정보 저장
        userStudyService.saveUserStudy(writer, savedStudy, true);

        //return
        return savedStudy.getId();
    }


    //조회
    public List<StudyResponse> findAllStudies(){
        List<Study> allStudies = studyRepository.findAll();

        //DTO로 변환
        List<StudyResponse> result = allStudies.stream()
                .map(study -> new StudyResponse(
                        study.getId(),
                        study.getTitle(),
                        study.getStart_date(),
                        study.getEnd_date(),
                        study.getMax_participants_num(),
                        study.getCur_participants_num(),
                        study.getStudyCategories().stream()
                                .map(studyCategory -> studyCategory.getCategory().getName())
                                .collect(Collectors.toList()),
                        study.getAdditionalInfos().stream()
                                .map(additionalInfo -> additionalInfo.getName())
                                .collect(Collectors.toList()))
                )
                .collect(Collectors.toList());
        return result;
    }

    //조회 - 필터 조건 추가
    public  List<StudyFilterResponse> findFilteredStudy(StudyFilterCondition filterCondition){
        List<StudyFilterResponse> FilteredStudyList = studyRepository.filterStudy(filterCondition);
        return FilteredStudyList;
    }

    /*
    public Study findStudy(Long id){
        Study findStudy = studyRepository.findById(id).orElse(null);
        return findStudy;
    }

    //수정
    @Transactional
    public Long updateStudy(Long id, StudyUpdateRequest studyUpdateRequest) {
        Study findStudy = studyRepository.findById(id).orElse(null);

        // JPA의 영속성 컨텍스트에 의해 entity 객체의 값만 변경하면 자동으로 변경 사항 반영하여 update 진행.
        // reoisitory.update 필요 없음.
        if(findStudy != null){
            findStudy.updateBoard(studyUpdateRequest.getTitle(), studyUpdateRequest.getContent());
        }
        // TODO: null 예외 처리

        return id;
    }
    //삭제
    @Transactional
    public Long deleteStudy(Long id){
        Study findStudy = studyRepository.findById(id).orElse(null);
        studyRepository.delete(findStudy);

        return id;
    }
    */
}
