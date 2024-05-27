package com.example.swip.service;

import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.chat.ChatProfileRequest;
import com.example.swip.dto.study.PostStudyAddMemberRequest;
import com.example.swip.dto.study.PostStudyDeleteMemberRequest;
import com.example.swip.dto.study.PostStudyRequest;
import com.mysema.commons.lang.Pair;

public interface ChatServerService {
    Pair<String, Integer> postUser(ChatProfileRequest chatProfileRequest);
    Pair<String, Integer> updateUser(ChatProfileRequest chatProfileRequest);
    Pair<String, Integer> deleteUser(Long userId);

    DefaultResponse postStudy(PostStudyRequest postStudyRequest);

    DefaultResponse addStudyMember(PostStudyAddMemberRequest postStudymemberRequest, String bearerToken);

    DefaultResponse deleteStudyMember(PostStudyDeleteMemberRequest postStudymemberRequest, String bearerToken);
}
