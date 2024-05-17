package com.example.swip.dto.todo;

import com.example.swip.entity.Study;
import com.example.swip.entity.StudyTodo;
import com.example.swip.entity.StudyTodoPublic;
import com.example.swip.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostTodoRequest {
    private String content;
    private LocalDate date;
    public StudyTodo toStudyTodo(Study study, User user) {
        return StudyTodo.builder()
                .content(this.content)
                .date(this.date)
                .user(user)
                .study(study)
                .build();
    }
    public StudyTodo toStudyTodoPublic(Study study, User user, StudyTodoPublic studyTodoPublic) {
        LocalDate temp = this.date;
        if(temp == null)
            temp = LocalDate.now();
        return StudyTodo.builder()
                .content(this.content)
                .date(temp)
                .study_todo_public(studyTodoPublic)
                .user(user)
                .study(study)
                .build();
    }
}
