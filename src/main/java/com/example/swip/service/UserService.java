package com.example.swip.service;



import com.example.swip.dto.UserMainProfileDto;
import com.example.swip.dto.UserRelatedStudyCount;
import com.example.swip.dto.auth.AddUserRequest;
import com.example.swip.dto.auth.PostProfileDto;
import com.example.swip.dto.study.StudyFilterResponse;
import com.example.swip.entity.Study;
import com.example.swip.entity.User;
import com.example.swip.entity.enumtype.StudyProgressStatus;
import com.example.swip.repository.UserRepository;
import com.example.swip.repository.UserRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRepositoryCustom userRepositoryCustom;

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
        findUser.updateProfile(postProfileDto.getNickname(), postProfileDto.getProfileImage());
        return true;
    }
    public UserMainProfileDto getMainProfileByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname);
        return getMainProfile(user);
    }
    public UserMainProfileDto getMainProfileById(Long user_id) {
        User user = userRepository.findById(user_id).orElse(null);
        return getMainProfile(user);
    }
    public UserMainProfileDto getMainProfile(User user) {
        return UserMainProfileDto.builder()
                .nickname(user.getNickname())
                .profile_img(user.getProfile_image())
                .email(user.getEmail())
                .user_id(user.getId())
                .build();
    }

    public List<Study> getProposerStudyList(Long userId) {
        return userRepositoryCustom.proposerStudyList(userId);
    }
    public List<Study> getRegisteredStudyList(Long userId, String status) {
        StudyProgressStatus.Element statusEnum = StudyProgressStatus.toStudyProgressStatusType(status);
        return userRepositoryCustom.registeredStudyList(userId, statusEnum);
    }

    public UserRelatedStudyCount getRelatedStudyNum(Long user_id) {
        UserRelatedStudyCount urscount = new UserRelatedStudyCount();
        urscount.setIn_progress(userRepositoryCustom.countInUserStudy(user_id, false));
        urscount.setIn_complete(userRepositoryCustom.countInUserStudy(user_id, true));
        urscount.setIn_favorite(userRepositoryCustom.countFavorite(user_id));
        urscount.setIn_proposal(userRepositoryCustom.countProposer(user_id));
        return urscount;
    }
    public UserRelatedStudyCount getPublicRelatedStudyNum(Long user_id) {
        UserRelatedStudyCount urscount = new UserRelatedStudyCount();
        urscount.setIn_progress(userRepositoryCustom.countInUserStudy(user_id, false));
        urscount.setIn_complete(userRepositoryCustom.countInUserStudy(user_id, true));
        return urscount;
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

    @Transactional
    public Long withdrawal(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user != null) {
            String email = user.getEmail();
            String validate = user.getValidate();
            userRepository.deleteById(id);
            User savedUser = userRepository.save(User.builder()
                            .email(email)
                            .validate(validate)
                            .withdrawal_date(LocalDateTime.now().plusDays(30))
                    .build());
            return savedUser.getId();
        }else
            return null;
    }

    @Transactional
    public void deleteExpiredUserData(LocalDateTime time) {
        userRepositoryCustom.deleteExpiredUserData(time);
    }

    public String deleteUser(Long id) {
        if(userRepository.existsById(id))
            userRepository.deleteById(id);
        return "delete success";
    }
}