package com.example.swip.api;

import com.example.swip.dto.StudyResponse;
import com.example.swip.dto.StudySaveRequest;
import com.example.swip.dto.StudyUpdateRequest;
import com.example.swip.entity.Study;
import com.example.swip.service.StudyService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class StudyApiController {

    private final StudyService studyService;

    //저장
    @PostMapping("/study")
    public Long saveStudy(@RequestBody StudySaveRequest dto){
        Long savedBoard = studyService.saveStudy(dto);
        System.out.println("savedBoard = " + savedBoard);
        return savedBoard;
    }

    //조회
    @GetMapping("/study")
    public Result showStudy(){
        List<Study> allStudies = studyService.findAllStudies();

        //Dto 로 변환
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

        return new Result(result); // TODO: Result 타입으로 한번 감싸기
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
    }

}
