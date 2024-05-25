package com.example.swip.service;

import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.JoinRequest.JoinRequestResponse;
import com.example.swip.dto.study.PostStudyAddMemberRequest;
import com.example.swip.entity.JoinRequest;
import com.example.swip.entity.Study;
import com.example.swip.entity.User;
import com.example.swip.entity.UserStudy;
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
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JoinRequestService {

    private final JoinRequestRepository joinRequestRepository;
    private final UserStudyService userStudyService;
    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final ChatServerService chatServerService;

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

    public List<JoinRequestResponse> getAllByStudyId(Long studyId) {
        List<JoinRequest> findJoinRequests = joinRequestRepository.findAllByStudyId(studyId);
        return findJoinRequests.stream()
                .map(request -> {
                    return JoinRequestResponse.builder()
                            .study_id(request.getId().getStudyId())
                            .user_id(request.getId().getUserId())
                            .join_status(request.getJoin_status().toString())
                            .request_date(request.getRequest_date())
                            .nickname(request.getUser().getNickname())
                            .profile_image(request.getUser().getProfile_image())
                            .build();
                }).collect(Collectors.toList());
    }

    public Integer getAllWaitingCountByStudyId(Long studyId) {
        List<JoinRequest> allWaitingByStudyId = joinRequestRepository.findAllWaitingByStudyId(studyId);
        return allWaitingByStudyId.size();
    }

    @Transactional
    public void acceptJoinRequest(Long studyId, Long userId, String bearerToken) {
        //join_status update
        JoinRequest findRequest = joinRequestRepository.findById(new JoinRequestId(userId, studyId)).orElse(null);
        if (findRequest != null) {
            findRequest.updateJoinStatus(JoinStatus.Approved);
        }

        //user_study에 추가
        User findUser = userRepository.findById(userId).orElse(null);
        Study findStudy = studyRepository.findById(studyId).orElse(null);

        if(findUser != null && findStudy != null) {
            UserStudy userStudy = userStudyService.saveUserStudy(findUser, findStudy, false);
            findStudy.updateCurParticipants("+", 1);
            //채팅방 멤버 추가 (chat server 연동)
            if(userStudy != null) {
                DefaultResponse defaultResponse = chatServerService.addStudyMember(
                        PostStudyAddMemberRequest.builder()
                                .token(bearerToken)
                                .studyId(studyId)
                                .userId(userId)
                                .type("accept") //방장이 허가 -> body userId 초대
                                .build()
                );
            }
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

    @Transactional
    public boolean cancelJoinRequest(Long userId, Long studyId) {
        JoinRequestId id = new JoinRequestId(userId, studyId);
        JoinRequest findRequest = joinRequestRepository.findById(id).orElse(null);
        if (findRequest == null) {
            return false;
        }
        else if(findRequest.getJoin_status() != JoinStatus.Waiting) {
            return false;
        }
        else{
            joinRequestRepository.deleteById(id);
            return true;
        }
    }

    @Transactional
    public void deleteExpiredJoinRequest(LocalDateTime time){
        joinRequestRepository.deleteExpiredJoinRequest(time);
    }

    public JoinStatus checkJoinStatusById(Long studyId, Long userId) {
        return joinRequestRepository.findJoinStatusById(new JoinRequestId(userId, studyId));
    }
}
