package com.example.swip.service;

import com.example.swip.config.security.JwtIssuer;
import com.example.swip.entity.User;
import com.example.swip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TestService {
    private final UserRepository userRepository;
    private final JwtIssuer jwtIssuer;
    public String getJWTByUserID(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null)
            return null;
        List<String> list = new LinkedList<>(Arrays.asList(user.getRole()));

        return jwtIssuer.issue(user.getId(),user.getEmail(),user.getValidate(),list);
    }
}
