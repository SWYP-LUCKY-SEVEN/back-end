package com.example.swip.api;

import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.*;
import com.example.swip.dto.quick_match.QuickMatchFilter;
import com.example.swip.dto.quick_match.QuickMatchResponse;
import com.example.swip.service.StudyQuickService;
import com.example.swip.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StudyApiController {

    private final StudyService studyService;
    private final StudyQuickService studyQuickService;

    //저장
    @Operation(summary = "스터디 생성 메소드",
            description = "스터디 생성 메소드입니다.[Athentication token 필요 - Baerer 타입]" +
                    "/ category: 정해진 분야(수능, 대학생, 코딩 ... 등 11가지) 중 선택된 1개의 값 문자열 형태로 넣기." +
                    "/ tags: 추가정보(태그)를 배열 형태로 넣기." +
                    "/ duration: (미정: x, 일주일: 1w, 한 달: 1m, 3개월: 3m, 6개월: 6m) 같이 문자열의 형태로 넣기" +
                    "/ max_participants_num : 최대 참여 인원" +
                    "/ matching_tye: 스터디 신청 방식 - (빠른 매칭: quick or 인증제: approval) - 문자열로 넣기" +
                    "/ tendency: 스터디 성향: (활발한 대화와 동기부여 원해요: active, 학습 피드백을 주고 받고 싶어요: feedback, 조용히 집중하고 싶어요: focus)- 문자열로 넣기")
    @PostMapping("/study")
    public Long saveStudy(
            @AuthenticationPrincipal UserPrincipal principal, // 권한 인증
            @RequestBody StudySaveRequest dto
    ){
        Long writerId = principal.getUserId();
        System.out.println("writerId = " + writerId);
        Long saveStudy = studyService.saveStudy(dto, writerId);
        return saveStudy;
    }

    //조회
    @Operation(summary = "스터디 전체 리스트 조회 메소드")
    @GetMapping("/study")
    public Result showStudy(){
        List<StudyResponse> allStudies = studyService.findAllStudies();
        int totalCount = allStudies.size(); //전체 리스트 개수
        return new Result(allStudies, totalCount); // TODO: Result 타입으로 한번 감싸기
    }

    // 조회 - 필터링
    @Operation(summary = "신규/전체/마감임박 스터디 리스트 필터링 & 정렬 메소드",
                description = "{type}: recent/ all/ deadline 중 하나로 작성(각각 신규, 전체, 마감임박 페이지) " +
                        "/ requestParam으로 필터링 조건 작성. 각각은 모두 Null 허용. 모두 null이면 필터가 걸리지 않은 상태 " +
                        "/ 검색기능 => queryString에 검색어 작성 (ex. '모각코')" +
                        "/ 검색 : 로그인 한 유저(token 필요), 로그인 x 유저(token 필요x)" +
                        "/ quickMatch는 빠른 매칭 선택시 'quick'으로 작성" +
                        "/ category는 카테고리 (ex. '코딩')" +
                        "/ minParticipants: 최소인원, maxParticipants: 최대인원" +
                        "/ tendency: active, feedback, focus (여러개 선택시 ,로 연결하여 입력): " +
                        "/ 마지막 orderType에 정렬 조건 넣기. 최근 등록순: recent, 인기순: popular, 마감 임박순: deadline, 가나다순: abd")
    @GetMapping("/study/{type}/filter")
    public Result filterAndSortStudy(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("type") String pageType,
            @RequestParam(required = false) String queryString, //검색어
            @RequestParam(required = false) String quickMatch,  //빠른 매칭 / 승인제
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) String duration,
            @RequestParam(required = false) Integer minParticipants,
            @RequestParam(required = false) Integer maxParticipants,
            @RequestParam(required = false) List<String> tendency, //active, feedback, focus
            @RequestParam(required = false) String orderType)
    {
        // 필터링 조건 객체 생성
        StudyFilterCondition filterCondition = StudyFilterCondition.builder()
                .page_type(pageType)
                .query_string(queryString)
                .quick_match(quickMatch)
                .category(category)
                .start_date(startDate)
                .duration(duration)
                .min_participants(minParticipants)
                .max_participants(maxParticipants)
                .tendency(tendency)
                .order_type(orderType)
                .build();

        // 필터링된 결과 리스트
        List<StudyFilterResponse> filteredStudy = new ArrayList<>();

        //로그인된 사용자인 & 검색어가 존재 하는 경우
        if(queryString!=null && userDetails != null) {
            UserPrincipal principal = (UserPrincipal) userDetails;
            filteredStudy = studyService.findQueryAndFilteredStudy(filterCondition, principal.getUserId());
        } else { //필터링만 or 알 수 없는 사용자
            filteredStudy = studyService.findFilteredStudy(filterCondition);
        }

        int totalCount = filteredStudy.size(); //전체 리스트 개수
        return new Result(filteredStudy, totalCount); // TODO: Result 타입으로 한번 감싸기
    }
    @Operation(summary = "저장된 빠른 매칭 가져오기")
    @GetMapping("/study/quick/filter")
    public QuickMatchFilter getQuickFilter(
            @AuthenticationPrincipal UserPrincipal principal // 권한 인증
    )
    {
        if (principal == null)
            return null;
        Long user_id = principal.getUserId();
        QuickMatchFilter quickMatchFilter = studyQuickService.getQuickMatchFilter(user_id);
        return quickMatchFilter;
    }
    @Operation(summary = "빠른 매칭 저장하기")
    @PostMapping("/study/quick/filter")
    public QuickMatchFilter postQuickFilter(
            @AuthenticationPrincipal UserPrincipal principal, // 권한 인증
            @RequestBody QuickMatchFilter quickMatchFilter
    )
    {
        if (principal != null) {
            Long user_id = principal.getUserId();
            studyQuickService.saveQuickMatchFilter(quickMatchFilter, user_id);
        }
        return quickMatchFilter;
    }
    @Operation(summary = "빠른 매칭 (필터 저장 X - 기존 필터도 삭제)",
            description = "pageType : recent/ all/ deadline/ nonApproval 중 하나로 작성(각각 신규, 전체, 마감임박, 승인없는 페이지) / requestParam으로 필터링 조건 작성. 각각은 모두 Null 허용. 모두 null이면 필터가 걸리지 않은 상태 / 마지막 orderType에 정렬 조건 넣기(ex. 최신 등록순)")
    @GetMapping("/study/quick/filter/{page}")
    public Result quickMatchStudy(
            @AuthenticationPrincipal UserPrincipal principal, // 권한 인증
            @PathVariable("page") Long page,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) String duration,
            @RequestParam(value="mem_scope", required = false) List<Long> mem_scope,
            @RequestParam(required = false) String tendency
    )
    {
        // 필터링 조건 객체 생성
        QuickMatchFilter quickMatchFilter = QuickMatchFilter.builder()
                .category(category)
                .start_date(startDate)
                .duration(duration)
                .tendency(tendency)
                .mem_scope(mem_scope)   //0: 1대1, 1: 3명~5명, 2: 6명~10명, 3: 11명 초과
                .build();

        // 필터링된 결과 리스트
        List<QuickMatchResponse> filteredStudy = studyQuickService.quickFilteredStudy(quickMatchFilter, page);
        int totalCount = filteredStudy.size(); //전체 리스트 개수

        return new Result("filteredStudy",totalCount);
    }
    @Operation(summary = "빠른 매칭 (필터 저장 O)")
    @PostMapping("/study/quick/filter/{page}")
    public Result quickMatchStudyAndSave(
            @AuthenticationPrincipal UserPrincipal principal, // 권한 인증
            @PathVariable("page") Long page,
            @RequestBody QuickMatchFilter quickMatchFilter)
    {
        if (principal == null)
            return null;
        Long user_id = principal.getUserId();
        // 필터링된 결과 리스트
        List<QuickMatchResponse> filteredStudy = studyQuickService.quickFilteredStudy(quickMatchFilter, page);
        studyQuickService.saveQuickMatchFilter(quickMatchFilter, user_id);

        int totalCount = filteredStudy.size(); //전체 리스트 개수

        return new Result(filteredStudy, totalCount);
    }

    @Operation(summary = "빠른 매칭 (필터 저장 X - 기존 필터도 삭제)")
    @DeleteMapping("/study/quick/filter/{page}")
    public Result quickMatchStudyAndDelete(
            @AuthenticationPrincipal UserPrincipal principal, // 권한 인증
            @PathVariable("page") Long page,
            @RequestBody QuickMatchFilter quickMatchFilter)
    {
        if (principal == null)
            return null;
        Long user_id = principal.getUserId();
        // 기존 필터 삭제
        studyQuickService.deleteQuickMatchFilter(user_id);
        // 퀵 매칭 정보 반환
        List<QuickMatchResponse> filteredStudy = studyQuickService.quickFilteredStudy(quickMatchFilter, page);
        int totalCount = filteredStudy.size();

        return new Result(filteredStudy,totalCount);
    }

    /**
     * 조회 - 스터디 상세 (보류)
     */
    /*
    @GetMapping("/study/{id}")
    public StudyDetailResponse showBoardDetail(@PathVariable("id") Long boardId){
        Study findStudy = studyService.findStudy(boardId);

        // Dto 로 변환
        StudyDetailResponse response = new StudyDetailResponse(
                findStudy.getId(),
                findStudy.getTitle(),
                findStudy.getContent(),
                findStudy.getWriter().getId(),
                findStudy.getCreated_time(),
                findStudy.getUpdated_time());

        return response;
    }

    //수정
    @PutMapping("/study/{id}/edit")
    public Long updateBoardDetail(
            @PathVariable("id") Long boardId,
            @RequestBody StudyUpdateRequest studyUpdateRequest
    ){
        Long updatedBoardId = studyService.updateStudy(boardId, studyUpdateRequest);
        return updatedBoardId;
    }

    //삭제
    @DeleteMapping("/study/{id}")
    public Long deleteBoardDetail(@PathVariable("id") Long boardId){
        Long deletedBoardId = studyService.deleteStudy(boardId);
        return deletedBoardId;
    }*/

    // List 값을 Result로 한 번 감싸서 return하기 위한 class
    @Data
    @AllArgsConstructor
    public static class Result<T>{
        private T data;
        private int totalCount;
    }

}
