package com.example.swip.dto.todo;

import com.example.swip.dto.study.StudyDetailMembers;
import com.example.swip.dto.user.SimpleUserProfileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyMBOResponse { //스터디 목표관리 페이지
    private LocalDate date;
    private Integer percent;
    private Integer group_percent;
    private List<StudyDetailMembers> membersList;
    private MemberTodoResponse member_todo;
}
