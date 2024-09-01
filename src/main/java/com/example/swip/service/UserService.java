package com.example.swip.service;



import com.example.swip.dto.study.StudyFilterResponse;
import com.example.swip.dto.user.UserMainProfileDto;
import com.example.swip.dto.user.UserRelatedStudyCount;
import com.example.swip.dto.auth.AddUserRequest;
import com.example.swip.dto.user.PostProfileDto;
import com.example.swip.entity.Evaluation;
import com.example.swip.entity.Study;
import com.example.swip.entity.User;
import com.example.swip.entity.enumtype.ChatStatus;
import com.example.swip.entity.enumtype.StudyProgressStatus;
import com.example.swip.repository.EvaluationRepository;
import com.example.swip.repository.UserRepository;
import com.example.swip.repository.custom.UserRepositoryCustom;
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
    private final EvaluationRepository evaluationRepository;

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
    public User updateProfile(PostProfileDto postProfileDto){
        User findUser = userRepository.findById(postProfileDto.getUser_id()).orElse(null);
        String temp = postProfileDto.getNickname().replaceAll("[^가-힣a-zA-Z0-9]","");
        if(findUser == null || postProfileDto.getNickname().length() != temp.length())
            return null;
        findUser.updateProfile(postProfileDto.getNickname(), postProfileDto.getProfileImage());
        return findUser;
    }
    public UserMainProfileDto getMainProfileByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname);
        return getMainProfile(user);

    }
    @Transactional
    public boolean evaluationUser(Long toId, Long fromId, Integer score) {
        User toUser = userRepository.findById(toId).orElse(null);
        if(100 < score || score < 0 || toUser == null )
            return false;
        evaluationRepository.save(Evaluation.builder()
                .rating(score)
                .to_user(toUser)
                .from_id(fromId)
                .build());
        return true;
    }

    public Integer getUserRatingByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname);
        if(user == null)
            return null;
        return getUserRating(user.getId());
    }

    public Integer getUserRating(Long userId) {
        List<Integer> evalList =userRepositoryCustom.getUserEvalList(userId);
        if (evalList == null || evalList.isEmpty())
            return null;
        int sum = 0;
        for(int eval : evalList) {
            sum += eval;
        }
        return sum/evalList.size();
    }

    public UserMainProfileDto getMainProfileById(Long user_id) {
        User user = userRepository.findById(user_id).orElse(null);
        return getMainProfile(user);
    }
    public UserMainProfileDto getMainProfile(User user) {
        Integer rating = getUserRating(user.getId());
        return UserMainProfileDto.builder()
                .nickname(user.getNickname())
                .profile_img(user.getProfile_image())
                .email(user.getEmail())
                .user_id(user.getId())
                .rating(rating)
                .build();
    }

    public UserRelatedStudyCount getRelatedStudyNum(Long user_id) {
        UserRelatedStudyCount urscount = new UserRelatedStudyCount();
        urscount.setIn_progress(
                userRepositoryCustom.countInUserStudy(user_id, StudyProgressStatus.Element.InProgress)
                + userRepositoryCustom.countInUserStudy(user_id,StudyProgressStatus.Element.BeforeStart));
        urscount.setIn_complete(userRepositoryCustom.countInUserStudy(user_id, StudyProgressStatus.Element.Done));
        urscount.setIn_favorite(userRepositoryCustom.countFavorite(user_id));
        urscount.setIn_proposal(userRepositoryCustom.countProposer(user_id));
        return urscount;
    }
    public UserRelatedStudyCount getPublicRelatedStudyNum(Long user_id) {
        UserRelatedStudyCount urscount = new UserRelatedStudyCount();
        urscount.setIn_progress(userRepositoryCustom.countInUserStudy(user_id, StudyProgressStatus.Element.InProgress));
        urscount.setIn_complete(userRepositoryCustom.countInUserStudy(user_id, StudyProgressStatus.Element.Done));
        return urscount;
    }

    //조회
    public User findUserById(Long writerId) {
        User findUser = userRepository.findById(writerId).orElse(null);
        return findUser;
    }

    // 저장
    @Transactional
    public User saveTestUser(AddUserRequest addUserRequest){
        User savedUser = userRepository.save(addUserRequest.toTestEntity());
        return savedUser;
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
    public boolean existUserId(Long id) {
        return userRepository.existsById(id);
    }
}