package com.example.swip.service;

import com.example.swip.dto.JoinRequest.JoinRequestResponse;
import com.example.swip.dto.study.PostStudyAddMemberRequest;
import com.example.swip.dto.study.PostStudyDeleteMemberRequest;
import com.example.swip.entity.JoinRequest;
import com.example.swip.entity.Study;
import com.example.swip.entity.User;
import com.example.swip.entity.UserStudy;
import com.example.swip.entity.compositeKey.JoinRequestId;
import com.example.swip.entity.enumtype.ExitStatus;
import com.example.swip.entity.enumtype.JoinStatus;
import com.example.swip.repository.JoinRequestRepository;
import com.example.swip.repository.StudyRepository;
import com.example.swip.repository.UserRepository;
import com.mysema.commons.lang.Pair;
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
    private final UserRepository userRepository;
    private final StudyRepository studyRepository;

    private final UserStudyService userStudyService;
    private final ChatServerService chatServerService;

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

            //채팅방 멤버 추가 (chat server 연동)
            if(userStudy != null) {
                ChatAddMemberDataSync(studyId, userId, bearerToken);
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
    public boolean cancelJoinRequest(String token, Long userId, Long studyId) {

        ExitStatus exitStatus = userStudyService.getExitStatus(userId, studyId);
        if(exitStatus != null && !userStudyService.getExitStatus(userId, studyId).equals(ExitStatus.None)){ //강퇴, 이탈 멤버는 취소 불가
            return false;
        }

        if(userStudyService.isStudyOwner(studyId, userId)){ //방장은 취소 불가
            return false;
        }

        JoinRequestId id = new JoinRequestId(userId, studyId);
        JoinRequest findRequest = joinRequestRepository.findById(id).orElse(null);
        Boolean isJoin = userStudyService.getAlreadyJoin(userId, studyId);

        if(isJoin && findRequest == null){ // 스터디 참가자 (빠른매칭)
            // 1. user_study 테이블에서 삭제
            userStudyService.updateExitStatus(userId, studyId, ExitStatus.Leave);
            ChatDeleteMemberDataSync(token, userId, studyId);
            return true;
        }
        if(isJoin && findRequest != null){ // 스터디 참가자 (승인제 : 신청 수락이 된 경우)
            // 1. join_request 테이블에서 삭제
            joinRequestRepository.deleteById(id);
            // 2. user_study 테이블에서 삭제
            userStudyService.updateExitStatus(userId, studyId, ExitStatus.Leave);
            ChatDeleteMemberDataSync(token, userId, studyId);
            return true;
        }
        if(!isJoin && findRequest.getJoin_status() == JoinStatus.Waiting){ // 신청 수락 대기중인 경우
            // 1. join_request 테이블에서 삭제
            joinRequestRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteExpiredJoinRequest(LocalDateTime time){
        joinRequestRepository.deleteExpiredJoinRequest(time);
    }

    public JoinStatus checkJoinStatusById(Long studyId, Long userId) {
        return joinRequestRepository.findJoinStatusById(new JoinRequestId(userId, studyId));
    }

    //==서비스 내부 로직==//

    private void ChatAddMemberDataSync(Long studyId, Long userId, String token) {
        chatServerService.addStudyMember(
                PostStudyAddMemberRequest.builder()
                        .token(token)
                        .studyId(studyId.toString())
                        .userId(userId.toString())
                        .type("accept") //방장이 허가 -> body userId 초대
                        .build()
        );
    }

    private void ChatDeleteMemberDataSync(String token, Long userId, Long studyId) {
        Pair<String, Integer> response = chatServerService.deleteStudyMemberSelf(
                PostStudyDeleteMemberRequest.builder()
                        .token(token)
                        .studyId(studyId.toString())
                        .userId(userId.toString())
                        .build()
        );
        System.out.println("chat data sync - member self deleted response = " + response);
    }
}
