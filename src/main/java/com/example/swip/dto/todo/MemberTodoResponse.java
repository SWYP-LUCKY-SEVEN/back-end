package com.example.swip.dto.todo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberTodoResponse {
    private Integer total_num;
    private Integer incomple_num;
    private Integer complete_num;
    private Integer percent;
    private List<TodoDto> public_todos;
    private List<TodoDto> personal_todos;
}
