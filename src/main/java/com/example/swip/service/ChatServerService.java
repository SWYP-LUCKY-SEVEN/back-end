package com.example.swip.service;

import com.example.swip.dto.auth.PostProfileDto;

public interface ChatServerService {
    boolean postUser(PostProfileDto postProfileDto);
}
