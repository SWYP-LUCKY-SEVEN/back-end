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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserWithdrawalService {
    private final UserRepository userRepository;
    private final UserStudyRepository userStudyRepository;
    private final StudyRepository studyRepository;

    @Transactional
    public Pair<Integer, Long> withdrawal(Long userId, Boolean isForce) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null)
            return new Pair(401, null);

        //참가중인 모든 스터디 반환
        List<UserStudy> userStudyList = userStudyRepository.findStudyByUserId(userId);

        for(UserStudy userStudy : userStudyList) {
            if(userStudy.is_owner()) {
                if(isForce)
                    studyRepository.deleteById(userStudy.getId().getStudyId());
                else
                    return new Pair(403, userStudy.getId().getStudyId());
            }
            userStudy.getStudy().updateCurParticipants("-", 1);
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
}
