package com.example.swip.service;

import com.example.swip.dto.DefaultResponse;
import com.example.swip.entity.Study;
import com.example.swip.entity.User;
import com.example.swip.entity.enumtype.MatchingType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyJoinService {
    private final StudyService studyService;
    private final UserService userService;
    private final UserStudyService userStudyService;

    @Transactional
    public ResponseEntity joinStudy(Long studyId, Long userId) {
        Study study = studyService.findStudyById(studyId);
        User user = userService.findUserById(userId);
        if(study==null || user==null)
            return ResponseEntity.status(404).body(DefaultResponse.builder()
                    .message("존재하지 않는 식별자입니다.")
                    .build());
        if(userStudyService.getAlreadyJoin(userId, studyId))
            return ResponseEntity.status(202).body(DefaultResponse.builder()
                    .message("이미 참가중인 사용자입니다.")
                    .build());

        if(study.getMatching_type().equals(MatchingType.Element.Quick)) {
            userStudyService.saveUserStudy(user, study, false);
            return ResponseEntity.status(200).body(DefaultResponse.builder()
                    .message("스터디에 참가되었습니다.")
                    .build());
        }else {
            return ResponseEntity.status(204).body(DefaultResponse.builder()
                    .message("즉시 가입을 허용하는 스터디가 아닙니다.")
                    .build());
        }
    }
}
