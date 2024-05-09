package com.example.swip.service;

import com.example.swip.dto.study.StudyFilterCondition;
import com.example.swip.dto.study.StudyFilterResponse;
import com.example.swip.dto.study.StudyResponse;
import com.example.swip.dto.study.StudySaveRequest;
import com.example.swip.entity.*;
import com.example.swip.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final AdditionalInfoService additionalInfoService;
    private final UserStudyService userStudyService;
    private final SearchService searchService;
    private final UserSearchService userSearchService;

    //저장
    @Transactional
    public Long saveStudy(StudySaveRequest studySaveRequest, Long writerId){
        //작성자 정보 조회
        User writer = userService.findUserById(writerId); //작성자 정보 조회

        //category id 조회
        Category findCategory = categoryService.findCategoryIdByName(studySaveRequest.getCategory());

        //study 저장
        Study savedStudy = studyRepository.save(
                studySaveRequest.toStudyEntity(findCategory)
        );

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
                        study.getCategory().getName(),
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

    //조회 - 필터 & 검색어
    @Transactional
    public  List<StudyFilterResponse> findQueryAndFilteredStudy(StudyFilterCondition filterCondition, Long writerId){
        //검색어 저장
        handleSearch(filterCondition, writerId);
        //제목, additional Info가 검색어와 일치하는 study 모두 return
        List<StudyFilterResponse> FilteredStudyList = studyRepository.filterStudy(filterCondition);
        return FilteredStudyList;
    }


    //서비스 코드 내부에서 사용되는 로직
    private void handleSearch(StudyFilterCondition filterCondition, Long writerId){
        if(filterCondition.getQuery_string()!=null && writerId!=null) {
            //검색어 중복 검색 -> 없으면 검색어 table에 저장 , search_user table에 저장
            String queryString = filterCondition.getQuery_string();
            Boolean isExist = searchService.KeywordIsExist(queryString);
            if (!isExist) { //검색어가 없으면, 검색어 table에 저장 & search_user table에 저장
                User user = userService.findUserById(writerId); //작성자 정보 조회

                searchService.saveKeyword(queryString);
                Search findKeyword = searchService.findByKeyword(queryString);
                userSearchService.saveSearchLog(findKeyword, user);
            } else if (isExist) { //검색어가 있으면, user_search에 있는지 조회/ 없으면 search_user table에만 저장 / 있으면 count 증가, updatetime 갱신
                User user = userService.findUserById(writerId); //작성자 정보 조회

                Search findKeyword = searchService.findByKeyword(queryString); //검색어 조회

                Optional<UserSearch> findUserSearch = userSearchService.findById(writerId, findKeyword.getId()); //작성자별 검색어 정보 조회

                //search_user 에 있으면, search 정보 업데이트 - count +1 , update_time
                if(findUserSearch.isPresent()) {
                    userSearchService.updateSearchLog(findKeyword, user);
                } else {  //search_user 에 없으면,  search_user 에 저장
                    userSearchService.saveSearchLog(findKeyword, user);
                }
                // 두 경우 모두, total count 증가
                findKeyword.updateCount();
            }
        }
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
