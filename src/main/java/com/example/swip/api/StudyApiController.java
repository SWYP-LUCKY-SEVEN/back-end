package com.example.swip.api;

import com.example.swip.config.security.UserPrincipal;
import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.quick_match.QuickMatchFilter;
import com.example.swip.dto.quick_match.QuickMatchResponse;
import com.example.swip.dto.study.*;
import com.example.swip.entity.Study;
import com.example.swip.service.*;
import com.mysema.commons.lang.Pair;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class StudyApiController {

    private final StudyService studyService;
    private final StudyQuickService studyQuickService;
    private final FavoriteStudyService favoriteStudyService;
    private final ChatServerService chatServerService;
    private final UserStudyService userStudyService;

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

        Study findStudy = studyService.findStudyById(saveStudy);
        if (findStudy!=null){ //채팅 서버에 저장
            Pair<String, Integer> response = chatServerService.postStudy(
                    PostStudyRequest.builder()
                            .studyId(findStudy.getId().toString())
                            .pk(writerId.toString())
                            .name(findStudy.getTitle())
                            .build()
            );
            System.out.println("postStudyResponse = " + response.getSecond());
            //TODO: 채팅 서버에 저장되었는지 여부 확인 후 조치
        }

        return saveStudy;
    }

    //조회
    @Operation(summary = "스터디 전체 리스트 조회 메소드")
    @GetMapping("/study")
    public Result showStudy(){
        List<Study> allStudies = studyService.findAllStudies();

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

        int totalCount = result.size(); //전체 리스트 개수
        return new Result(result, totalCount); // TODO: Result 타입으로 한번 감싸기
    }

    // 조회 - 필터링
    @Operation(summary = "신규/전체/마감임박 스터디 리스트 필터링 & 정렬 메소드",
            description = "{type}: recent/ all/ deadline 중 하나로 작성(각각 신규, 전체, 마감임박 페이지)\n\n" +
                    "requestParam으로 필터링 조건 작성. 각각은 모두 Null 허용. 모두 null이면 필터가 걸리지 않은 상태\n\n" +
                    "검색기능 => queryString에 검색어 작성 (ex. '모각코')\n" +
                    "- 검색 : 로그인 한 유저(token 필요), 로그인 x 유저(token 필요x)\n" +
                    "- quickMatch는 빠른 매칭 선택시 'quick'으로 작성\n" +
                    "- category는 카테고리 (ex. '코딩', '수능', '대학생', '취업', '공무원', '임용', " +
                    "'전문직', '어학', '자격증', '코딩', '모각공', '기타')\n" +
                    "- minParticipants: 최소인원\n" +
                    "- maxParticipants: 최대인원\n" +
                    "- tendency: active, feedback, focus (여러개 선택시 ,로 연결하여 입력): \n" +
                    "- orderType(정렬 조건) : recent(최근 등록순), popular(인기순), deadline(마감 임박순), abcd(가나다순)\n")
    @GetMapping("/study/{type}/filter")
    public Result filterAndSortStudy(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "페이지",
                    in = ParameterIn.PATH,
                    schema = @Schema(defaultValue = "recent",
                            allowableValues = {"recent", "all", "deadline", "nonApproval"}))
            @PathVariable("type") String pageType,
            @RequestParam(required = false) String queryString, //검색어
            @Parameter(description = "참가 방식",
                    in = ParameterIn.QUERY,
                    schema = @Schema(defaultValue = "approval",
                            allowableValues = {"quick", "approval"}))
            @RequestParam(required = false) String quickMatch,  //빠른 매칭 / 승인제
            @Parameter(description = "분야",
                    in = ParameterIn.QUERY,
                    schema = @Schema(defaultValue = "수능",
                            allowableValues = {"수능", "대학생", "취업", "공무원", "임용",
                                    "전문직", "어학", "자격증", "코딩", "모각공", "기타"}))
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "기간",
                    in = ParameterIn.QUERY,
                    schema = @Schema(defaultValue = "1w",
                            allowableValues = {"1w", "1m", "3m", "6m"}))
            @RequestParam(required = false) String duration,
            @RequestParam(required = false) Integer minParticipants,
            @RequestParam(required = false) Integer maxParticipants,
            @Parameter(description = "성향",
                    in = ParameterIn.QUERY,
                    array = @ArraySchema(schema = @Schema(
                            allowableValues = {"active", "feedback", "focus"}),
                            minItems = 0, maxItems = 3, uniqueItems = true))
            @RequestParam(required = false) List<String> tendency, //active, feedback, focus
            @Parameter(description = "정렬",
                    in = ParameterIn.QUERY,
                    schema = @Schema(defaultValue = "recent",
                            allowableValues = {"recent", "popular", "deadline", "abc"}))
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
    @Operation(summary = "스터디 진행 상태 변경 (방장 권한 필요)",
            description = "status = before, progress, done")
    @PatchMapping("/study/{study_id}/status")
    public ResponseEntity<DefaultResponse> patchStudyStatus(
            @AuthenticationPrincipal UserPrincipal principal, // 권한 인증
            @PathVariable("study_id") Long study_id,
            @RequestParam String status
    )
    {
        if (principal == null)
            return ResponseEntity.status(403).build();
        Long user_id = principal.getUserId();

        int respone_status = studyService.updateStudyStatus(study_id, user_id, status);

        return ResponseEntity.status(respone_status).body(DefaultResponse.builder()
                        .message("성공적입니다.")
                .build());
    }
    @Operation(summary = "저장된 빠른 매칭 가져오기")
    @GetMapping("/study/quick/filter")
    public ResponseEntity<QuickMatchFilter> getQuickFilter(
            @AuthenticationPrincipal UserPrincipal principal // 권한 인증
    )
    {
        if (principal == null)
            return ResponseEntity.status(403).build();
        Long user_id = principal.getUserId();
        QuickMatchFilter quickMatchFilter = studyQuickService.getQuickMatchFilter(user_id);
        if (quickMatchFilter == null)
            return ResponseEntity.status(201).build();

        return ResponseEntity.status(200).body(quickMatchFilter);
    }
    @Operation(summary = "빠른 매칭 - 상위 리스트 9개 반환 (JWT 필요)",
            description = "page : 다시 매칭한 횟수\n" +
                    "1. Save 옵션 True시 조건 저장. false시 조건 삭제\n" +
                    "2. 일치하는 조건은 (분야 > 시작일 > 진행기간 > 성향 > 인원) 순으로 정렬된다.\n" +
                    "3. mem_scope : 0 : 2명, 1 : 3~5명, 2 : 6~10명, 3: 11~20명")
    @PostMapping("/study/quick/match")
    public Result postQuickMatchStudy(
            @AuthenticationPrincipal UserPrincipal principal, // 권한 인증
            @RequestParam boolean save,
            @Parameter(description = "분야",
                    in = ParameterIn.QUERY,
                    schema = @Schema(defaultValue = "수능",
                            allowableValues = {"수능", "대학생", "취업", "공무원", "임용",
                                    "전문직", "어학", "자격증", "코딩", "모각공", "기타"}))
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "기간",
                    in = ParameterIn.QUERY,
                    schema = @Schema(defaultValue = "1w",
                            allowableValues = {"1w", "1m", "3m", "6m"}))
            @RequestParam(required = false) String duration,
            @Parameter(description = "0: 2명, 1: 3~5명, 2: 6~10명, 3: 11명 이상",
                    in = ParameterIn.QUERY,
                    array = @ArraySchema(schema = @Schema(type = "integer", format = "int64",
                            allowableValues = {"0", "1", "2", "3"}),
                            minItems = 0, maxItems = 4, uniqueItems = true))
            @RequestParam(required = false) List<Long> mem_scope,
            @Parameter(description = "성향",
                    in = ParameterIn.QUERY,
                    array = @ArraySchema(schema = @Schema(
                            allowableValues = {"active", "feedback", "focus"}),
                            minItems = 0, maxItems = 3, uniqueItems = true))
            @RequestParam(required = false) List<String> tendency
    )
    {
        if(principal == null)
            return null;
        Long user_id = principal.getUserId();

        // 필터링 조건 객체 생성
        QuickMatchFilter quickMatchFilter = QuickMatchFilter.builder()
                .quick_match("quick")
                .category(category)
                .start_date(startDate)
                .duration(duration)
                .tendency(tendency)
                .mem_scope(mem_scope)
                .build();

        if(save == true)
            studyQuickService.saveQuickMatchFilter(quickMatchFilter, user_id);
        else
            studyQuickService.deleteQuickMatchFilter(user_id);

        // 필터링된 결과 리스트
        List<QuickMatchResponse> filteredStudyList =
                studyQuickService.quickFilteredStudy(
                        quickMatchFilter,
                        user_id,
                        0L,
                        9L);

        int totalCount = filteredStudyList.size(); //전체 리스트 개수

        return new Result(filteredStudyList,totalCount);
    }

    @Operation(summary = "찜 추가",
            description = "스터디 찜 추가")
    @PostMapping("/study/{study_id}/favorite")
    public ResponseEntity postFavoriteStudy(
            @AuthenticationPrincipal UserPrincipal userPrincipal, // 권한 인증
            @PathVariable("study_id") Long studyId
    ) {
        if(userPrincipal == null)
            return ResponseEntity.status(403).body(
                    DefaultResponse.builder()
                            .message("로그인이 필요합니다.")
                            .build());

        boolean status = favoriteStudyService.postFavoriteStudy( userPrincipal.getUserId(), studyId);
        if(status)
            return ResponseEntity.status(200).body(
                    DefaultResponse.builder()
                            .message("success")
                            .build());

        return ResponseEntity.status(404).body(
                DefaultResponse.builder()
                        .message("올바르지 않은 ID")
                        .build());
    }
    @Operation(summary = "찜 삭제",
            description = "스터디 찜 제거")
    @DeleteMapping("/study/{study_id}/favorite")
    public ResponseEntity deleteFavoriteStudy(
            @AuthenticationPrincipal UserPrincipal userPrincipal, // 권한 인증
            @PathVariable("study_id") Long studyId
    ) {
        if(userPrincipal == null)
            return ResponseEntity.status(403).body(
                    DefaultResponse.builder()
                            .message("로그인이 필요합니다.")
                            .build());

        boolean status = favoriteStudyService.deleteFavoriteStudy(userPrincipal.getUserId(), studyId);
        if(status)
            return ResponseEntity.status(200).body(
                    DefaultResponse.builder()
                            .message("success")
                            .build());

        return ResponseEntity.status(404).body(
                DefaultResponse.builder()
                        .message("올바르지 않은 ID")
                        .build());
    }


    @Operation(summary = "스터디 참가 (정식 기능)",
            description = "스터디 참가 신청. 빠른 매칭 지원 스터디일 경우 즉시 참가. 승인제일 경우 신청 생성.\n" +
                    "- 200 : 이미 참가 신청 혹은 참가" +
                    "- 200 : 꽉 찬 스터디")
    @PostMapping("/study/join/{study_id}")
    public ResponseEntity joinStudy(
            @AuthenticationPrincipal UserPrincipal userPrincipal, // 권한 인증
            @PathVariable("study_id") Long studyId
    ) {
        if(userPrincipal == null)
            return ResponseEntity.status(403).body(
                    DefaultResponse.builder()
                            .message("로그인이 필요합니다.")
                            .build());

        return studyService.joinStudy(studyId, userPrincipal.getUserId(), userPrincipal.getToken());
    }

    /**
     * 조회 - 스터디 상세
     */
    @Operation(summary = "스터디 상세 정보 조회 API")
    @GetMapping("/study/{study_id}")
    public StudyDetailResponse showBoardDetail(@PathVariable("study_id") Long studyId){
        StudyDetailResponse studyDetail = studyService.findStudyDetailAndUpdateViewCount(studyId);

        return studyDetail;
    }


    //수정
    @Operation(summary = "스터디 조회 API (수정용) - 수정 시에 스터디 내용 불러오기")
    @GetMapping("/study/{study_id}/edit")
    public ResponseEntity<Result2> getStudyEditPage(
        @AuthenticationPrincipal UserPrincipal userPrincipal,
        @PathVariable("study_id") Long studyId
    ){
        Long ownerId = userPrincipal.getUserId();
        Long findStudyOwner = userStudyService.getOwnerbyStudyId(studyId);
        if(!ownerId.equals(findStudyOwner)) {
            return ResponseEntity.status(403).body(new Result2<>(null, "스터디를 수정할 권한이 없습니다."));
        }
        StudyUpdateResponse response = studyService.findStudyEditDetailById(studyId);
        return ResponseEntity.status(200).body(new Result2<>(response, "성공"));
    }

    @Operation(summary = "스터디 수정 API")
    @PatchMapping("/study/{study_id}")
    public ResponseEntity<String> updateStudyDetail(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("study_id") Long studyId,
            @RequestBody StudyUpdateRequest studyUpdateRequest
    ){
        Long ownerId = userPrincipal.getUserId();
        Long findStudyOwner = userStudyService.getOwnerbyStudyId(studyId);
        if(!ownerId.equals(findStudyOwner)) {
            return ResponseEntity.status(403).body("스터디를 수정할 권한이 없습니다.");
        }
        Boolean updateStatus = studyService.updateStudy(userPrincipal.getToken(), studyId, studyUpdateRequest);
        if(updateStatus){
            return ResponseEntity.status(200).body("스터디 수정 완료!");
        }
        return ResponseEntity.status(404).body("스터디 수정이 불가능합니다");
    }

    //삭제
    @Operation(summary = "스터디 삭제 API")
    @DeleteMapping("/study/{study_id}")
    public ResponseEntity<String> deleteStudy(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("study_id") Long studyId
    ){
        Long ownerId = userPrincipal.getUserId();
        Long findStudyOwner = userStudyService.getOwnerbyStudyId(studyId);
        if(!ownerId.equals(findStudyOwner)) {
            return ResponseEntity.status(403).body("스터디를 삭제할 권한이 없습니다.");
        }

        boolean deletedStatus = studyService.deleteStudy(userPrincipal.getToken(), studyId);
        if(deletedStatus){
            return ResponseEntity.status(200).body("삭제 성공!");
        }
        return ResponseEntity.status(404).body("존재하지 않는 스터디");
    }

    // List 값을 Result로 한 번 감싸서 return하기 위한 class
    @Data
    @AllArgsConstructor
    public static class Result<T>{
        private T data;
        private Integer totalCount;
    }
    @Data
    @AllArgsConstructor
    public static class Result2<T>{
        private T data;
        private String message;
    }

}