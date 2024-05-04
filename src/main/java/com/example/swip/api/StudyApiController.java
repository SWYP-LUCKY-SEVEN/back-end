package com.example.swip.api;

import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.*;
import com.example.swip.entity.Study;
import com.example.swip.service.StudyQuickService;
import com.example.swip.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                    "/ duration: (미정, 일주일, 한 달, 3개월, ...) 같이 문자열의 형태로 넣기" +
                    "/ max_participants_num : 최대 참여 인원" +
                    "/ matching_tye: 스터디 신청 방식 - (빠른 매칭 or 인증제) - 문자열로 넣기" +
                    "/ tendency: 스터디 성향: (활발한 대화와 동기부여 원해요), ... - 문자열로 넣기")
    @PostMapping("/study")
    public ResponseEntity<Long> saveStudy(
            @AuthenticationPrincipal UserPrincipal principal, // 권한 인증
            @RequestBody StudySaveRequest dto
    ){
        Long writerId = principal.getUserId();
        if(principal != null) {
            Long saveStudy = studyService.saveStudy(dto, writerId);
            return ResponseEntity.ok(saveStudy);
        }
        return ResponseEntity.status(403).build();
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
    @Operation(summary = "신규/전체/마감임박/승인없음 스터디 리스트 필터링 & 정렬 메소드",
                description = "pageType : recent/ all/ deadline/ nonApproval 중 하나로 작성(각각 신규, 전체, 마감임박, 승인없는 페이지) / requestParam으로 필터링 조건 작성. 각각은 모두 Null 허용. 모두 null이면 필터가 걸리지 않은 상태 / 마지막 orderType에 정렬 조건 넣기(ex. 최신 등록순)")
    @GetMapping("/study/{type}/filter")
    public Result filterAndSortStudy(
            @PathVariable("type") String pageType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) String duration,
            @RequestParam(required = false) Integer minParticipants,
            @RequestParam(required = false) Integer maxParticipants,
            @RequestParam(required = false) String tendency,
            @RequestParam(required = false) String orderType)
    {
        // 필터링 조건 객체 생성
        StudyFilterCondition filterCondition = StudyFilterCondition.builder()
                .pageType(pageType)
                .category(category)
                .start_date(startDate)
                .duration(duration)
                .min_participants(minParticipants)
                .max_participants(maxParticipants)
                .tendency(tendency)
                .order_type(orderType)
                .build();

        // 필터링된 결과 리스트
        List<StudyFilterResponse> filteredStudy = studyService.findFilteredStudy(filterCondition);

        int totalCount = filteredStudy.size(); //전체 리스트 개수
        return new Result(filteredStudy, totalCount); // TODO: Result 타입으로 한번 감싸기
        //return filteredStudy;
    }
    @Operation(summary = "신규/전체/마감임박/승인없음 스터디 리스트 필터링 & 정렬 메소드",
            description = "pageType : recent/ all/ deadline/ nonApproval 중 하나로 작성(각각 신규, 전체, 마감임박, 승인없는 페이지) / requestParam으로 필터링 조건 작성. 각각은 모두 Null 허용. 모두 null이면 필터가 걸리지 않은 상태 / 마지막 orderType에 정렬 조건 넣기(ex. 최신 등록순)")
    @GetMapping("/study/quick/filter")
    public Result filterAndSortStudy(
            @AuthenticationPrincipal UserPrincipal principal, // 권한 인증
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) String duration,
            @RequestParam(required = false) String participants_scope,
            @RequestParam(required = false) String tendency,
            @RequestParam(required = false) boolean remember)
    {
        Long writerId = principal.getUserId();
        // 필터링 조건 객체 생성
        QuickMatchDto quickMatchDto = QuickMatchDto.builder()
                .category(category)
                .start_date(startDate)
                .duration(duration)
                .tendency(tendency)
                .participants_scope(participants_scope)
                .build();

        // 필터 저장 여부 선택시
        if(remember)
            studyQuickService.saveQuickMatchFilter(quickMatchDto, writerId);
            //save
        // 필터링된 결과 리스트
        // List<StudyFilterResponse> filteredStudy = studyService.findFilteredStudy(filterCondition);
        //int totalCount = filteredStudy.size(); //전체 리스트 개수
        //return new Result(filteredStudy, totalCount); // TODO: Result 타입으로 한번 감싸기

        return new Result("test",3);
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
