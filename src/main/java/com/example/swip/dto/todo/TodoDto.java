package com.example.swip.dto.todo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodoDto {
    private Long id;
    private String content;
    private boolean complete;
    private Long todo_parent_id;
}
