package com.example.swip.service;

import com.example.swip.entity.User;
import com.example.swip.entity.UserStudy;
import com.example.swip.repository.StudyRepository;
import com.example.swip.repository.UserRepository;
import com.example.swip.repository.UserStudyRepository;
import com.mysema.commons.lang.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserWithdrawalService {
    private final UserRepository userRepository;
    private final UserStudyRepository userStudyRepository;
    private final StudyRepository studyRepository;
    private final UserService userService;

    private UserStudy getNewOnwer(UserStudy userStudy) {
        UserStudy result = null;
        int high_score = 0;
        List<UserStudy> studyUserList = userStudyRepository.findStudyByStudyIdExceptOwner(userStudy.getId().getStudyId());
        for(UserStudy studyUser : studyUserList) {
            Integer temp = userService.getUserRating(studyUser.getId().getUserId());
            if(temp != null && high_score < temp) {
                high_score = temp;
                result = studyUser;
            }
        }
        if(high_score == 0 || result == null) {
            Random random = new Random();
            random.setSeed(System.currentTimeMillis());
            int size = studyUserList.size();
            result = studyUserList.get(random.nextInt(size));
        }
        return result;
    }
    @Transactional
    private boolean deleteStudyAction (UserStudy userStudy, boolean isForce) {
        if(userStudy.is_owner()) {
            if(isForce) {
                if(userStudy.getStudy().getCur_participants_num() > 1) {
                    getNewOnwer(userStudy).setIs_owner(true);
                } else {
                    studyRepository.deleteById(userStudy.getId().getStudyId());
                }
            }else
                return false;
        }
        userStudy.getStudy().updateCurParticipants("-", 1);
        return true;
    }
    @Transactional
    public Pair<Integer, Long> withdrawal(Long userId, Boolean isForce) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null)
            return new Pair(401, null);

        //참가중인 모든 스터디 반환
        List<UserStudy> userStudyList = userStudyRepository.findStudyByUserId(userId);

        for(UserStudy userStudy : userStudyList) {
            if(!deleteStudyAction(userStudy, isForce))
                return new Pair(403, userStudy.getId().getStudyId());
        }

        String email = user.getEmail();
        String validate = user.getValidate();

        userRepository.deleteById(userId);
        User savedUser = userRepository.save(User.builder()
                .email(email)
                .validate(validate)
                .withdrawal_date(LocalDateTime.now().plusDays(30))
                .build());
        return new Pair(201, userId);
    }
    @Transactional
    public Pair<Integer, Long> deleteUser(Long userId, Boolean isForce) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null)
            return new Pair(401, null);

        //참가중인 모든 스터디 반환
        List<UserStudy> userStudyList = userStudyRepository.findStudyByUserId(userId);

        for(UserStudy userStudy : userStudyList) {
            if(!deleteStudyAction(userStudy, isForce))
                return new Pair(403, userStudy.getId().getStudyId());
        }

        userRepository.deleteById(userId);
        return new Pair(201, userId);
    }
}