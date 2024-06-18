package com.example.swip.repository;

import com.example.swip.entity.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import static com.example.swip.entity.QStudyTodo.studyTodo;

@Repository
@RequiredArgsConstructor
public class StudyTodoRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    //해당 유저의 todo list 반환 (공통 할일 포함) (개수도 포함)
    public List<StudyTodo> getMemberTodolist(Long study_id, Long user_id, LocalDate date) {
        List<StudyTodo> studyTodoList = queryFactory
                .select(studyTodo)
                .from(studyTodo)
                .where(studyTodo.study.id.eq(study_id), studyTodo.user.id.eq(user_id), studyTodo.date.eq(date))
                .fetch();
        return studyTodoList;
    }
    public Long getMemberTodolistCount(Long study_id, Long user_id, LocalDate date) {
        Long count = queryFactory
                .select(studyTodo.count())
                .from(studyTodo)
                .where(studyTodo.study.id.eq(study_id), studyTodo.user.id.eq(user_id), studyTodo.date.eq(date))
                .fetchOne();
        return count;
    }
    public Long getCompleteTodolistCount(Long study_id, Long user_id, LocalDate date) {
        Long count = queryFactory
                .select(studyTodo.count())
                .from(studyTodo)
                .where(studyTodo.study.id.eq(study_id),
                        studyTodo.user.id.eq(user_id),
                        studyTodo.date.eq(date),
                        studyTodo.complete.eq(true))
                .fetchOne();
        return count;
    }

    //해당 스터디 전체의 개인 todo 개수 반환
    public Long getMemberTodoNumInGroup(Long study_id, LocalDate date) {
        return queryFactory
                .select(studyTodo.count())
                .from(studyTodo)
                .where(studyTodo.study.id.eq(study_id), studyTodo.date.eq(date))
                .fetchOne();
    }

    //해당 스터디 전체의 개인 todo 완료수 반환
    public Long getCompleteTodoNumInGroup(Long study_id, LocalDate date) {
        return queryFactory
                .select(studyTodo.count())
                .from(studyTodo)
                .where(studyTodo.study.id.eq(study_id), studyTodo.date.eq(date), studyTodo.complete.eq(true))
                .fetchOne();
    }

    //해당 그룹 목표 관련 개인 목표 삭제
    public Long deleteAllByGroupTodo(Long parent_id) {
        return queryFactory
                .delete(studyTodo)
                .where(studyTodo.study_todo_public.id.eq(parent_id))
                .execute();
    }

    //해당 그룹 목표 관련 개인 목표 삭제
    public Long updateAllByGroupTodo(Long parent_id, String new_text) {
        return queryFactory
                .update(studyTodo)
                .set(studyTodo.content, new_text)
                .where(studyTodo.study_todo_public.id.eq(parent_id))
                .execute();
    }

}
