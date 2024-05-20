package com.example.swip.service;


import com.example.swip.dto.study.*;
import com.example.swip.dto.todo.MemberTodoResponse;
import com.example.swip.dto.userStudy.UserProgressStudyResponse;
import com.example.swip.entity.*;
import com.example.swip.dto.*;
import com.example.swip.entity.Category;
import com.example.swip.entity.Search;
import com.example.swip.entity.Study;
import com.example.swip.entity.User;
import com.example.swip.entity.enumtype.MatchingType;
import com.example.swip.entity.enumtype.StudyProgressStatus;
import com.example.swip.repository.StudyRepository;
import com.example.swip.repository.StudyTodoRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final JoinRequestService joinRequestService;
    private final ChatServerService chatServerService;
    private final StudyTodoRepositoryCustom studyTodoRepositoryCustom;

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
        if(studySaveRequest.getTags() != null)
            additionalInfoService.saveAddInfo(studySaveRequest.getTags(), savedStudy);

        //user_study - 방장 정보 저장
        userStudyService.saveUserStudy(writer, savedStudy, true);

        //return
        return savedStudy.getId();
    }


    //조회
    public List<Study> findAllStudies(){
        List<Study> allStudies = studyRepository.findAll();
        return allStudies;
    }

    //조회 - 필터 조건 추가
    public  List<StudyFilterResponse> findFilteredStudy(StudyFilterCondition filterCondition){
        List<StudyFilterResponse> FilteredStudyList = studyRepository.filterStudy(filterCondition);
        return FilteredStudyList;
    }

    //조회 - 필터 & 검색어
    @Transactional
    public List<StudyFilterResponse> findQueryAndFilteredStudy(StudyFilterCondition filterCondition, Long writerId){
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

    @Transactional
    public ResponseEntity joinStudy(Long studyId, Long userId, String bearerToken) {
        Study study = studyRepository.findById(studyId).orElse(null);
        User user = userService.findUserById(userId);
        if(study==null || user==null)
            return ResponseEntity.status(404).body(DefaultResponse.builder()
                    .message("존재하지 않는 식별자입니다.")
                    .build());
        if(userStudyService.getAlreadyJoin(userId, studyId))
            return ResponseEntity.status(400).body(DefaultResponse.builder()
                    .message("이미 참가중인 사용자입니다.")
                    .build());
        if(!(study.getMax_participants_num() > study.getCur_participants_num()))
            return ResponseEntity.status(400).body(DefaultResponse.builder()
                    .message("참가 인원이 꽉 찬 스터디입니다.")
                    .build());

        if(study.getMatching_type().equals(MatchingType.Element.Quick)) {
            UserStudy findUserStudy = userStudyService.saveUserStudy(user, study, false);
            //study entity의 cur_participants_num update
            study.updateCurParticipants("+", 1);
            //채팅방 멤버 추가 (chat server 연동)
            if (findUserStudy!=null) { //채팅 서버에 저장
                DefaultResponse defaultResponse = chatServerService.addStudyMember(
                        PostStudyAddMemberRequest.builder()
                                .token(bearerToken)
                                .studyId(study.getId())
                                .userId(user.getId())
                                .type("join") //본인이 참가 => 토큰에 있는 유저 초대
                                .build()
                );
                System.out.println("postStudyResponse = " + defaultResponse.getMessage());
            }
            return ResponseEntity.status(200).body(DefaultResponse.builder()
                    .message("쇼터디에 가입했어요.")
                    .build());
        } else { //approval
            //이미 스터디 참가 신청한 경우
            if(joinRequestService.getAlreadyRequest(userId, studyId)){
                return ResponseEntity.status(400).body(DefaultResponse.builder()
                        .message("이미 가입 신청한 사용자입니다.")
                        .build());
            }
            else { //처음 참가 신청하는 경우
                joinRequestService.saveJoinRequest(user, study);
                return ResponseEntity.status(200).body(DefaultResponse.builder()
                        .message("스터디 가입신청을 요청했어요.")
                        .build());
            }
        }
    }


    @Transactional
    public StudyDetailResponse findStudyDetailAndUpdateViewCount(Long StudyId){
        //study 상세 page에 나오는 모든 것들을 반환
        //study 상세 정보
        Study study = studyRepository.findStudyDetailById(StudyId);
        //study 멤버 정보
        List<UserStudy> allUsersByStudyId = userStudyService.getAllUsersByStudyId(StudyId);

        //view_count + 1
        study.updateViewcount();

        String category = study.getCategory().getName();
        List<String> tags = study.getAdditionalInfos().stream()
                .map(tag -> {
                    return tag.getName();
                }).collect(Collectors.toList());

        return StudyDetailResponse.builder()
                .title(study.getTitle())
                .description(study.getDescription())
                .tags(tags)
                .category(category)
                .matching_type(study.getMatching_type().toString())
                .start_date(study.getStart_date())
                .end_date(study.getEnd_date())
                .duration(study.getDuration())
                .max_participants_num(study.getMax_participants_num())
                .cur_participants_num(study.getCur_participants_num())
                .tendency(study.getTendency().toString())
                .membersList(
                        allUsersByStudyId.stream()
                                .map(member -> {
                                    return StudyDetailMembers.builder()
                                            .nickname(member.getUser().getNickname())
                                            .profileImage(member.getUser().getProfile_image())
                                            .is_owner(member.is_owner())
                                            .build();
                                })
                                .collect(Collectors.toList())
                )
                .build();
    }

    public Study findStudyById(Long id){
        return studyRepository.findById(id).orElse(null);
    }

    public List<StudyFilterResponse> studyListToStudyFilterResponse(List<Study> studyList) {
        List<StudyFilterResponse> responses = studyList.stream()
                .map(study -> new StudyFilterResponse(
                        study.getId(),
                        study.getTitle(),
                        StudyProgressStatus.toString(study.getStatus()),
                        study.getStart_date(),
                        study.getEnd_date(),
                        study.getMax_participants_num(),
                        study.getCur_participants_num(),
                        study.getCreated_time(),
                        study.getCategory().getName(),
                        study.getAdditionalInfos().stream()
                                .map(info -> info.getName())
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
        return responses;
    }
    @Transactional
    public int updateStudyStatus(Long studyId, Long userId, String status) {
        if(userStudyService.getOwnerbyStudyId(studyId) != userId)
            return 403;

        Study study = studyRepository.findById(studyId).orElse(null);
        if(study == null)
            return 404;
        study.updateStatus(StudyProgressStatus.toStudyProgressStatusType(status));

        return 200;
    }

    public List<StudyFilterResponse> getProposerStudyList(Long userId) {
        List<Study> list = userService.getProposerStudyList(userId);
        return studyListToStudyFilterResponse(list);
    }
    public List<StudyFilterResponse> getRegisteredStudyList(Long userId, String status) {
        List<Study> list = userService.getRegisteredStudyList(userId, status);
        return studyListToStudyFilterResponse(list);
    }
    public MemberTodoResponse getProgressTodo(Long studyId, Long userId, LocalDate date) {
        int total_num = studyTodoRepositoryCustom.getMemberTodolistCount(studyId, userId, date).intValue();
        int complete_num = studyTodoRepositoryCustom.getCompleteTodolistCount(studyId, userId, date).intValue();
        int percent = 0;
        if (total_num != 0)
            percent = (complete_num*100)/total_num;

        return MemberTodoResponse.builder()
                .total_num(total_num)
                .complete_num(complete_num)
                .percent(percent)
                .build();
    }
    public List<UserProgressStudyResponse> getProgressStudyList(Long userId) {
        List<Study> studyList = userService.getRegisteredStudyList(userId, "progress");
        LocalDate now = LocalDate.now();

        List<UserProgressStudyResponse> result = studyList.stream()
                .map(study -> {
                    MemberTodoResponse progress_todo = getProgressTodo(study.getId(), userId, now);
                    return new UserProgressStudyResponse(
                            study.getId(),
                            study.getTitle(),
                            study.getCategory().getName(),
                            StudyProgressStatus.toString(study.getStatus()),
                            study.getStart_date(),
                            study.getEnd_date(),
                            study.getMax_participants_num(),
                            study.getCur_participants_num(),
                            progress_todo
                    );
                })
                .collect(Collectors.toList());
        return result;
    }
  
    @Transactional
    public void progressStartStudy(LocalDate date) {
        List<Study> studyList = studyRepository.progressStartStudy(date);
        studyList.forEach(study -> {
            System.out.println(study.getId());
            study.updateStatus(StudyProgressStatus.Element.InProgress);
        });
    }
    @Transactional
    public void completeExpiredStudy(LocalDate date) {
        List<Study> studyList = studyRepository.completeExpiredStudy(date);
        studyList.forEach(study -> {
            study.updateStatus(StudyProgressStatus.Element.Done);
        });
    }

    //삭제
    @Transactional
    public boolean deleteStudy(Long studyId){
        Optional<Study> findStudy = studyRepository.findById(studyId);
        if(findStudy.isPresent()) {
            studyRepository.deleteById(studyId);
            return true;
        }
        return false;
    }

    public StudyUpdateResponse findStudyEditDetailById(Long studyId) {
        Study study = studyRepository.findStudyEditDetailById(studyId);
        if(study!=null){
            return StudyUpdateResponse.builder()
                    .title(study.getTitle())
                    .description(study.getDescription())
                    .tags(study.getAdditionalInfos().stream()
                            .map(info -> info.getName())
                            .collect(Collectors.toList()))
                    .build();
        }
        return null;
    }

    @Transactional
    public Boolean updateStudy(Long studyId, StudyUpdateRequest studyUpdateRequest) {
        Study findStudy = studyRepository.findById(studyId).orElse(null);

        if(findStudy != null){
            findStudy.updateStudy(findStudy, studyUpdateRequest.getTitle(), studyUpdateRequest.getDescription(), studyUpdateRequest.getTags());
            return true;
        }
        return false;
    }

    public boolean isAlreadyFull(Long studyId) {
        Study findStudy = studyRepository.findById(studyId).orElse(null);
        int curNum = findStudy.getCur_participants_num();
        int maxNum = findStudy.getMax_participants_num();
        if(curNum==maxNum){
            return true;
        }else{
            return false;
        }
    }
}


