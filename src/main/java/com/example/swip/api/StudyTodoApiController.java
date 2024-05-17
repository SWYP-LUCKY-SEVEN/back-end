package com.example.swip.api;

import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.todo.CompleteTodoRequest;
import com.example.swip.dto.todo.PostTodoRequest;
import com.example.swip.dto.todo.StudyMBOResponse;
import com.example.swip.service.StudyTodoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class StudyTodoApiController {
    private final StudyTodoService studyTodoService;
    @Operation(summary = "스터디 내, 목표 관리 화면", description = "확인할 user id, study id, 확인할 일자(default : now) 입력")
    @GetMapping("/study/{study_id}/todo_management") // user id 반환
    public ResponseEntity<StudyMBOResponse> getStudyMBO(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("study_id") Long studyId,
            @RequestParam String nickname,
            @RequestParam(required = false) LocalDate date
    ){  // Authorization 내 principal 없으면 null 값
        if(userPrincipal == null)
            return ResponseEntity.status(403).build();

        if (date == null)
            date = LocalDate.now();
        StudyMBOResponse response = studyTodoService.getMemberTodoList(studyId, nickname, date);

        return ResponseEntity.status(200).body(response);
    }
    @Operation(summary = "공용 목표 생성", description = "")
    @PostMapping("/study/{study_id}/group_todo") // user id 반환
    public ResponseEntity<DefaultResponse> postGroupTodo(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("study_id") Long studyId,
            @RequestBody PostTodoRequest request_todo
    ){
        if(userPrincipal == null)
            return ResponseEntity.status(403).build();

        int status = studyTodoService.postGroupTodo(studyId, userPrincipal.getUserId(), request_todo);
        return ResponseEntity.status(status).body(DefaultResponse.builder()
                .message("테스트 문구입니다.")
                .build());
    }

    @Operation(summary = "공용 목표 삭제", description = "목표리스트를 반환할때 제공된 parent_todo_id가 반드시 필요함.")
    @DeleteMapping("/study/{study_id}/group_todo") // user id 반환
    public ResponseEntity<DefaultResponse> deleteGroupTodo(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("study_id") Long studyId,
            @RequestParam Long parent_todo_id
    ){
        if(userPrincipal == null)
            return ResponseEntity.status(403).build();

        int status = studyTodoService.deleteGroupTodo(studyId, userPrincipal.getUserId(), parent_todo_id);
        return ResponseEntity.status(status).body(DefaultResponse.builder()
                .message("테스트 문구입니다.")
                .build());
    }

    @Operation(summary = "개인 목표 생성", description = "")
    @PostMapping("/study/{study_id}/todo") // user id 반환
    public ResponseEntity<DefaultResponse> postPersonalTodo(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("study_id") Long studyId,
            @RequestBody PostTodoRequest request_todo
    ){
        if(userPrincipal == null)
            return ResponseEntity.status(403).build();

        int status = studyTodoService.postPersonalTodo(studyId, userPrincipal.getUserId(), request_todo);
        return ResponseEntity.status(status).body(DefaultResponse.builder()
                .message("테스트 문구입니다.")
                .build());
    }

    @Operation(summary = "개인 목표 삭제", description = "")
    @DeleteMapping("/study/{study_id}/todo") // user id 반환
    public ResponseEntity<DefaultResponse> deletePersonalTodo(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("study_id") Long studyId,
            @RequestParam Long todo_id
    ){
        if(userPrincipal == null)
            return ResponseEntity.status(403).build();

        int status = studyTodoService.deletePersonalTodo(studyId, userPrincipal.getUserId(), todo_id);
        return ResponseEntity.status(status).body(DefaultResponse.builder()
                .message("테스트 문구입니다.")
                .build());
    }

    @Operation(summary = "개인 목표 상태 변경", description = "")
    @PatchMapping("/study/{study_id}/todo") // user id 반환
    public ResponseEntity<DefaultResponse> patchPersonalTodo(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("study_id") Long studyId,
            @RequestBody CompleteTodoRequest todoRequest
    ){
        if(userPrincipal == null)
            return ResponseEntity.status(403).build();

        int status = 0;
        if(todoRequest.isComplete()) {
            status = studyTodoService.completeTodo(studyId, userPrincipal.getUserId(), todoRequest.getTodo_id());
        }else {
            status = studyTodoService.inCompleteTodo(studyId, userPrincipal.getUserId(), todoRequest.getTodo_id());
        }
        return ResponseEntity.status(status).body(DefaultResponse.builder()
                .message("테스트 문구입니다.")
                .build());
    }
}
