package com.example.swip.service;


import com.example.swip.dto.AddUserRequest;
import com.example.swip.dto.BoardSaveRequest;
import com.example.swip.entity.Board;
import com.example.swip.entity.User;
import com.example.swip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }

    public boolean isDuplicatedUserName(String username) {
        var user = findByEmail(username);
        System.out.println(user);
        if(user == null)
            return false;
        else
            return true;
    }

    //조회
    public User findUserById(Long writerId) {
        User findUser = userRepository.findById(writerId).orElse(null);
        return findUser;
    }

    // 저장
    @Transactional
    public Long saveUser(AddUserRequest addUserRequest){
        User savedUser = userRepository.save(addUserRequest.toEntity());
        return savedUser.getId();
    }
}