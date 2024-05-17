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

    @Operation(summary = "개인 목표 생성", description = "개인 목표 생성. content에 목표를 입력하고, date에 생성할 날짜를 선택 (기본값 : 오늘)")
    @PostMapping("/study/{study_id}/todo") // user id 반환
    public ResponseEntity<DefaultResponse> postPersonalTodo(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("study_id") Long studyId,
            @RequestBody PostTodoRequest request_todo
    ){
        if(userPrincipal == null)
            return ResponseEntity.status(403).build();

        int status = studyTodoService.postPersonalTodo(studyId, userPrincipal.getUserId(), request_todo);
        String message = getResponseMessage(status);
        return ResponseEntity.status(status).body(DefaultResponse.builder()
                .message(message)
                .build());
    }

    @Operation(summary = "개인 목표 삭제", description = "개인 목표를 삭제하는 API 입니다. 수정할 목표의 ID 값이 필요합니다.")
    @DeleteMapping("/study/{study_id}/todo") // user id 반환
    public ResponseEntity<DefaultResponse> deletePersonalTodo(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("study_id") Long studyId,
            @RequestParam Long todo_id
    ){
        if(userPrincipal == null)
            return ResponseEntity.status(403).build();

        int status = studyTodoService.deletePersonalTodo(studyId, userPrincipal.getUserId(), todo_id);
        String message = getResponseMessage(status);
        return ResponseEntity.status(status).body(DefaultResponse.builder()
                .message(message)
                .build());
    }

    @Operation(summary = "목표 상태 변경", description = "목표 완료 상태를 수정하는 API 입니다. 수정할 목표의 ID 값이 필요합니다.")
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
        String message = getResponseMessage(status);
        return ResponseEntity.status(status).body(DefaultResponse.builder()
                .message(message)
                .build());
    }

    private String getResponseMessage(int status) {
        String message = "";
        switch(status) {
            case 403:
                message = "해당 스터디의 멤버가 아닙니다";
                break;
            case 404:
                message = "값이 유효하지 않습니다.";
                break;
            case 200:
                message = "success";
                break;
            case 201:
                message = "성공적으로 생성되었습니다.";
                break;
            default:
                message = "예외처리되지 못한 오류";
        }
        return message;
    }
}
