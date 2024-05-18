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
    private int total_num;
    private int incomple_num;
    private int complete_num;
    private int percent;
    private List<TodoDto> public_todos;
    private List<TodoDto> personal_todos;
}
