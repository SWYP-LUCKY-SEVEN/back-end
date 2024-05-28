package com.example.swip.service;

import com.example.swip.dto.study.StudyDetailMembers;
import com.example.swip.dto.todo.MemberTodoResponse;
import com.example.swip.dto.todo.PostTodoRequest;
import com.example.swip.dto.todo.StudyMBOResponse;
import com.example.swip.dto.todo.TodoDto;
import com.example.swip.entity.*;
import com.example.swip.repository.StudyTodoPublicRepository;
import com.example.swip.repository.StudyTodoRepository;
import com.example.swip.repository.StudyTodoRepositoryCustom;
import com.example.swip.repository.UserRepository;
import com.mysema.commons.lang.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyTodoService {
    private final StudyTodoRepository studyTodoRepository;
    private final UserRepository userRepository;
    private final StudyService studyService;
    private final StudyTodoPublicRepository studyTodoPublicRepository;
    private final StudyTodoRepositoryCustom studyTodoRepositoryCustom;
    private final UserStudyService userStudyService;

    private Pair<StudyTodo, Integer> isPermitted(Long study_id, Long user_id, Long todo_id){
        Study study = studyService.findStudyById(study_id);
        User user = userRepository.findById(user_id).orElse(null);
        StudyTodo todo = studyTodoRepository.findById(todo_id).orElse(null);
        if(study == null || user == null || todo == null)
            return new Pair<>(null, 404);
        if(todo.getUser().getId() != user_id ||
                todo.getStudy().getId() != study_id)
            return new Pair<>(null, 403); //해당 목표에 권한이 없음
        return new Pair<>(todo, 200);
    }


    //////////////////
    // 목표 공통 기능 //
    /////////////////
    //그룹 목표 완료.
    @Transactional
    public int completeTodo(Long study_id, Long user_id, Long todo_id) {
        Pair<StudyTodo, Integer> pair = isPermitted(study_id, user_id, todo_id);
        if (pair.getSecond() != 200)
            return pair.getSecond();
        //api
        StudyTodo todo = pair.getFirst();
        todo.updateComplete(true);
        return 200;
    }
    //todo 완료 취소
    @Transactional
    public int inCompleteTodo(Long study_id, Long user_id, Long todo_id) {
        Pair<StudyTodo, Integer> pair = isPermitted(study_id, user_id, todo_id);
        if (pair.getSecond() != 200)
            return pair.getSecond();
        //api
        StudyTodo todo = pair.getFirst();
        todo.updateComplete(false);
        return 200;
    }
    public StudyMBOResponse getMemberTodoList(Long studyId, String nickname, LocalDate date) {
        User user = userRepository.findByNickname(nickname);

        if(!userStudyService.isStudyOwner(studyId, user.getId())) //관리자가 아닐경우 null 반환
            return null;
        //해당 날짜 스터디 멤버의 모든 todo를 가져옴.
        List<StudyTodo> memberTodoList = studyTodoRepositoryCustom.getMemberTodolist(studyId, user.getId(), date);

        //MemberTodoResponse 생성에 필요한 데이터
        int mem_todo_total_size = memberTodoList.size();
        int mem_todo_comple_size = 0;
        List<TodoDto> public_todos = new ArrayList<>();
        List<TodoDto> personal_todos = new ArrayList<>();
        for(StudyTodo studyTodo : memberTodoList) {
            if(studyTodo.getStudy_todo_public() != null){
                public_todos.add(
                        TodoDto.builder()
                                .id(studyTodo.getId())
                                .content(studyTodo.getContent())
                                .complete(studyTodo.isComplete())
                                .build()
                );
            }else {
                personal_todos.add(
                        TodoDto.builder()
                                .id(studyTodo.getId())
                                .content(studyTodo.getContent())
                                .complete(studyTodo.isComplete())
                                .todo_parent_id(studyTodo.getStudy_todo_public().getId())
                                .build()
                );
            }
            if(studyTodo.isComplete())
                mem_todo_comple_size++;
        }
        MemberTodoResponse memberTodoResponse = MemberTodoResponse.builder()
                .total_num(mem_todo_total_size)
                .incomple_num(mem_todo_total_size-mem_todo_comple_size)
                .complete_num(mem_todo_comple_size)
                .public_todos(public_todos)
                .personal_todos(personal_todos)
                .build();

        //선택 스터디, 날짜, 선택 멤버
        Long group_todo_size = studyTodoRepositoryCustom.getMemberTodoNumInGroup(studyId, date);
        Long group_complete_size = studyTodoRepositoryCustom.getCompleteTodoNumInGroup(studyId, date);

        List<UserStudy> allUsersByStudyId = userStudyService.getAllUsersByStudyId(studyId);

        //개인 달성률 => 개인 Todo의 완료율
        int personal_percent = 0;
        if(mem_todo_total_size != 0)
            personal_percent = (mem_todo_comple_size*100)/ mem_todo_total_size;
        //그룹 달성률 => 전체 Todo의 완료율
        int group_percent = 0;
        if(group_todo_size != 0)
            group_percent = (group_complete_size.intValue()*100)/ group_todo_size.intValue();

        //합쳐서 반환
        return StudyMBOResponse.builder()
                .date(date)
                .percent(personal_percent)
                .group_percent(group_percent)
                .membersList(
                        allUsersByStudyId.stream()
                                .map(member -> {
                                    return StudyDetailMembers.builder()
                                            .nickname(member.getUser().getNickname())
                                            .profileImage(member.getUser().getProfile_image())
                                            .is_owner(member.is_owner())
                                            .build();
                                })
                                .collect(Collectors.toList())
                )
                .member_todo(memberTodoResponse)
                .build();
    }

    ///////////////////////
    // 그룹 공유 목표 관련  //
    ///////////////////////
    //그룹 공용 목표 생성.
    @Transactional
    public int postGroupTodo(Long study_id, Long user_id, PostTodoRequest request_todo) { //그룹 todo 추가
        Study study = studyService.findStudyById(study_id);
        User user = userRepository.findById(user_id).orElse(null);
        if(study == null || user == null)
            return 404;
        if(!userStudyService.isStudyOwner(study_id, user.getId()))
            return 403;

        StudyTodoPublic studyTodoPublic = studyTodoPublicRepository.save(
                StudyTodoPublic.builder()
                        .content(request_todo.getContent())
                        .date(request_todo.getDate())
                        .study(study)
                        .build()
        );
        //StudyTodoPublic에 추가
        List<UserStudy> allUsersByStudyId = userStudyService.getAllUsersByStudyId(study_id);  //검증된 user
        if(allUsersByStudyId == null)
            return 404;
        //스터디 참여 멤버 id를 모두 가져옴
        List<StudyTodo> studyTodos = new ArrayList<>();
        for(UserStudy member : allUsersByStudyId) {
            studyTodos.add(
                    request_todo.toStudyTodoPublic(
                            member.getStudy(),
                            member.getUser(),
                            studyTodoPublic)
            );
        }
        //각자의 StudyTodo에 추가.
        studyTodoRepository.saveAll(studyTodos);

        return 201;
    }
    //그룹 공용 목표 삭제 (방장만 사용가능) (삭제할땐 todo_parent_id가 필수다)
    @Transactional
    public int deleteGroupTodo(Long study_id, Long user_id, Long parent_id) {
        User user = userRepository.findById(user_id).orElse(null);
        if(user == null)
            return 401; //정상적인 유저가 아닙니다.
        //권한 확인.
        if(!userStudyService.isStudyOwner(study_id, user.getId()))
            return 403; //스터디의 관리자가 아닙니다.
        //공용 todo목록에서 id 가져옴.

        if(!studyTodoPublicRepository.existsById(parent_id))
            return 404; //존재하는 그룹 todo가 아닙니다.

        //해당 id 가진 목표 전부 삭제 (cascade가 알아서 해주겠지만 불안해서..)
        Long status = studyTodoRepositoryCustom.deleteAllByGroupTodo(parent_id);
        System.out.println(status);
        //공용 목록에서 삭제.
        studyTodoPublicRepository.deleteById(parent_id);
        return 200;
    }


    ///////////////////////
    // 그룹 개인 목표 관련  //
    ///////////////////////
    //개인 목표 생성.
    @Transactional
    public int postPersonalTodo(Long study_id, Long user_id, PostTodoRequest request_todo) {
        //해당 스터디, 해당 멤버, 해당 날짜의 StudyTodo를 생성
        Study study = studyService.findStudyById(study_id);
        User user = userRepository.findById(user_id).orElse(null);
        if(study == null || user == null)
            return 404;
        if(!userStudyService.getAlreadyJoin(user_id, study_id))
            return 403; //해당 스터디의 멤버가 아닙니다

        studyTodoRepository.save(request_todo.toStudyTodo(study, user));
        return 201;
    }

    //개인 목표 삭제
    @Transactional
    public int deletePersonalTodo(Long study_id, Long user_id, Long todo_id) {
        Pair<StudyTodo, Integer> pair = isPermitted(study_id, user_id, todo_id);
        if (pair.getSecond() != 200)
            return pair.getSecond();
        if(pair.getFirst().getStudy_todo_public() != null)
            return 403;
        //해당 studytodo id의 목표 제거
        studyTodoRepository.deleteById(todo_id);
        return 200;
    }
}
