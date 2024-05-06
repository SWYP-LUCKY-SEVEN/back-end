package com.example.swip.service;

import com.example.swip.entity.Study;
import com.example.swip.entity.User;
import com.example.swip.entity.UserStudy;
import com.example.swip.entity.compositeKey.UserStudyId;
import com.example.swip.entity.enumtype.ExitStatus;
import com.example.swip.repository.UserStudyRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserStudyService {

    private final UserStudyRepository userStudyRepository;

    //저장
    @Transactional
    public void saveUserStudy(User writer, Study savedStudy, boolean is_owner){

        UserStudy userStudy = UserStudy.builder()
                .id(new UserStudyId(writer.getId(), savedStudy.getId()))
                .user(writer)
                .study(savedStudy)
                .is_owner(is_owner)
                .exit_status(ExitStatus.None)
                .build();

        userStudyRepository.save(userStudy);
    }
}
