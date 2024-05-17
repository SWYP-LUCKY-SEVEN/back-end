package com.example.swip.dto.todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompleteTodoRequest {
    private Long todo_id;
    private boolean complete;
}
