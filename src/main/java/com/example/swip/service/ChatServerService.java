package com.example.swip.service;

import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.study.PostStudyAddMemberRequest;
import com.example.swip.dto.study.PostStudyDeleteMemberRequest;
import com.example.swip.dto.user.PostProfileDto;
import com.example.swip.dto.user.PostProfileResponse;
import com.example.swip.dto.study.PostStudyRequest;
import org.springframework.http.ResponseEntity;

public interface ChatServerService {
    ResponseEntity<DefaultResponse> postUser(PostProfileDto postProfileDto);
    ResponseEntity<DefaultResponse> updateUser(PostProfileDto postProfileDto);
    ResponseEntity<DefaultResponse> deleteUser(Long userId);

    DefaultResponse postStudy(PostStudyRequest postStudyRequest);

    DefaultResponse addStudyMember(PostStudyAddMemberRequest postStudymemberRequest);

    DefaultResponse deleteStudyMember(PostStudyDeleteMemberRequest postStudymemberRequest);
}
