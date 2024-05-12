package com.example.swip.service;

import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.auth.PostProfileDto;
import com.example.swip.dto.auth.PostProfileResponse;

public interface ChatServerService {
    PostProfileResponse postUser(PostProfileDto postProfileDto);
    PostProfileResponse updateUser(PostProfileDto postProfileDto);
    DefaultResponse deleteUser(Long userId);
}
