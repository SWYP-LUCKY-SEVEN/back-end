package com.example.swip.service;

import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.user.PostProfileDto;
import com.example.swip.dto.user.PostProfileResponse;
import com.example.swip.dto.study.PostStudyRequest;
import com.example.swip.dto.study.PostStudyResponse;

public interface ChatServerService {
    PostProfileResponse postUser(PostProfileDto postProfileDto);
    PostProfileResponse updateUser(PostProfileDto postProfileDto);
    DefaultResponse deleteUser(Long userId);

    PostStudyResponse postStudy(PostStudyRequest postStudyRequest);
}
