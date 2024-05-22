package com.example.swip.service;

import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.study.PostStudyAddMemberRequest;
import com.example.swip.dto.study.PostStudyDeleteMemberRequest;
import com.example.swip.dto.user.PostProfileDto;
import com.example.swip.dto.user.PostProfileResponse;
import com.example.swip.dto.study.PostStudyRequest;
import com.mysema.commons.lang.Pair;
import org.springframework.http.ResponseEntity;

public interface ChatServerService {
    Pair<String, Integer> postUser(PostProfileDto postProfileDto);
    Pair<String, Integer> updateUser(PostProfileDto postProfileDto);
    Pair<String, Integer> deleteUser(Long userId);

    DefaultResponse postStudy(PostStudyRequest postStudyRequest);

    DefaultResponse addStudyMember(PostStudyAddMemberRequest postStudymemberRequest);

    DefaultResponse deleteStudyMember(PostStudyDeleteMemberRequest postStudymemberRequest);
}
