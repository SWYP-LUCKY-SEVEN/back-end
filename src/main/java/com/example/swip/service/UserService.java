package com.example.swip.service;



import com.example.swip.dto.auth.AddUserRequest;
import com.example.swip.dto.auth.PostProfileDto;
import com.example.swip.entity.User;
import com.example.swip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }

    public boolean isDuplicatedNickname(String nickname) {
        User user = userRepository.findByNickname(nickname);
        System.out.println(user);
        if(user == null)
            return false;
        else
            return true;
    }

    @Transactional
    public boolean updateProfile(PostProfileDto postProfileDto){
        User findUser = userRepository.findById(postProfileDto.getUser_id()).orElse(null);
        String temp = postProfileDto.getNickname().replaceAll("[^가-힣a-zA-Z0-9]","");
        if(findUser == null || postProfileDto.getNickname().length() != temp.length())
            return false;
        findUser.createProfile(postProfileDto.getNickname(), postProfileDto.getProfileImage());
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