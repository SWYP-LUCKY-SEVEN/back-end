package com.example.swip.service;

import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.study.PostStudyAddMemberRequest;
import com.example.swip.dto.study.PostStudyDeleteMemberRequest;
import com.example.swip.dto.user.PostProfileDto;
import com.example.swip.dto.user.PostProfileResponse;
import com.example.swip.dto.study.PostStudyRequest;

public interface ChatServerService {
    PostProfileResponse postUser(PostProfileDto postProfileDto);
    PostProfileResponse updateUser(PostProfileDto postProfileDto);
    int deleteUser(Long userId);

    DefaultResponse postStudy(PostStudyRequest postStudyRequest);

    DefaultResponse addStudyMember(PostStudyAddMemberRequest postStudymemberRequest);

    DefaultResponse deleteStudyMember(PostStudyDeleteMemberRequest postStudymemberRequest);
}
