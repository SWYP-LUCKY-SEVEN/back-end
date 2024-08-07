package com.example.swip.service;

import com.example.swip.dto.chat.ChatProfileRequest;
import com.example.swip.dto.chat.DeleteStudyRequest;
import com.example.swip.dto.chat.UpdateStudyRequest;
import com.example.swip.dto.study.PostStudyAddMemberRequest;
import com.example.swip.dto.study.PostStudyDeleteMemberRequest;
import com.example.swip.dto.study.PostStudyRequest;
import com.mysema.commons.lang.Pair;

public interface ChatServerService {
    Pair<String, Integer> postUser(ChatProfileRequest chatProfileRequest);
    Pair<String, Integer> updateUser(ChatProfileRequest chatProfileRequest);
    Pair<String, Integer> deleteUser(Long userId);

    Pair<String, Integer> postStudy(PostStudyRequest postStudyRequest);
    Pair<String, Integer> updateStudy(UpdateStudyRequest postStudyRequest, Long userId);
    Pair<String, Integer> deleteStudy(DeleteStudyRequest deleteStudyRequest);

    Pair<String, Integer> addStudyMember(PostStudyAddMemberRequest postStudymemberRequest);
    Pair<String, Integer> deleteStudyMember(PostStudyDeleteMemberRequest postStudymemberRequest);
    Pair<String, Integer> deleteStudyMemberSelf(PostStudyDeleteMemberRequest postStudymemberRequest);
}
