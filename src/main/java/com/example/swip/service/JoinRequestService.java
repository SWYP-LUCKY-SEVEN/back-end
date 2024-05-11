package com.example.swip.service;

import com.example.swip.entity.JoinRequest;
import com.example.swip.entity.Study;
import com.example.swip.entity.User;
import com.example.swip.entity.compositeKey.JoinRequestId;
import com.example.swip.entity.enumtype.JoinStatus;
import com.example.swip.repository.JoinRequestRepository;
import com.example.swip.repository.StudyRepository;
import com.example.swip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JoinRequestService {

    private final JoinRequestRepository joinRequestRepository;
    private final UserStudyService userStudyService;
    private final UserRepository userRepository;
    private final StudyRepository studyRepository;


    public boolean getAlreadyRequest(Long userId, Long studyId) {
        return joinRequestRepository.existsById(new JoinRequestId(userId, studyId));
    }

    @Transactional
    public void saveJoinRequest(User user, Study study) {
        JoinRequest savedJoinRequest = joinRequestRepository.save(
                JoinRequest.builder()
                        .id(new JoinRequestId(user.getId(), study.getId()))
                        .user(user)
                        .study(study)
                        .request_date(LocalDateTime.now())
                        .join_status(JoinStatus.Waiting) //대기중
                        .build()
        );
    }

    public List<JoinRequest> getAllByStudyId(Long studyId) {
        return joinRequestRepository.findAllByStudyId(studyId);
    }

    public Integer getAllWaitingCountByStudyId(Long studyId) {
        List<JoinRequest> allWaitingByStudyId = joinRequestRepository.findAllWaitingByStudyId(studyId);
        return allWaitingByStudyId.size();
    }

    @Transactional
    public void acceptJoinRequest(Long studyId, Long userId) {
        //join_status update
        JoinRequest findRequest = joinRequestRepository.findById(new JoinRequestId(userId, studyId)).orElse(null);
        if (findRequest != null) {
            findRequest.updateJoinStatus(JoinStatus.Approved);
        }

        //user_study에 추가
        User findUser = userRepository.findById(userId).orElse(null);
        Study findStudy = studyRepository.findById(studyId).orElse(null);

        if(findUser != null && findStudy != null) {
            userStudyService.saveUserStudy(findUser, findStudy, false);
        }
    }

    @Transactional
    public void rejectJoinRequest(Long studyId, Long userId) {
        //join_status update
        JoinRequest findRequest = joinRequestRepository.findById(new JoinRequestId(userId, studyId)).orElse(null);
        if (findRequest != null) {
            findRequest.updateJoinStatus(JoinStatus.Rejected);
        }
    }
}