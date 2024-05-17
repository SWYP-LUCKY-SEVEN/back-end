package com.example.swip.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberTodoResponse {
    private int total_num;
    private int incomple_num;
    private int complete_num;
    private List<TodoDto> public_todos;
    private List<TodoDto> personal_todos;
}
