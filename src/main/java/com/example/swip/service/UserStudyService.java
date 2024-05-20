package com.example.swip.service;

import com.example.swip.dto.DefaultResponse;
import com.example.swip.dto.study.PostStudyAddMemberRequest;
import com.example.swip.dto.study.PostStudyDeleteMemberRequest;
import com.example.swip.entity.*;
import com.example.swip.entity.compositeKey.UserStudyExitId;
import com.example.swip.entity.compositeKey.UserStudyId;
import com.example.swip.entity.enumtype.ExitReason;
import com.example.swip.entity.enumtype.ExitStatus;
import com.example.swip.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserStudyService {

    private final UserStudyRepository userStudyRepository;
    private final ExitReasonsRepository exitReasonsRepository;
    private final UserStudyExitRepository userStudyExitRepository;
    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final ChatServerService chatServerService;


    //저장
    @Transactional
    public UserStudy saveUserStudy(User user, Study savedStudy, boolean is_owner){

        UserStudy userStudy = UserStudy.builder()
                .id(new UserStudyId(user.getId(), savedStudy.getId()))
                .user(user)
                .study(savedStudy)
                .is_owner(is_owner)
                .exit_status(ExitStatus.None)
                .join_date(LocalDateTime.now())
                .build();

        return userStudyRepository.save(userStudy);
    }
    public boolean getAlreadyJoin(Long userId, Long studyId) {
        return userStudyRepository.existsById(new UserStudyId(userId, studyId));
    }

    public List<UserStudy> getAllUsersByStudyId(Long studyId){
        List<UserStudy> allUsersByStudyId = userStudyRepository.findAllByStudyId(studyId);
        return allUsersByStudyId;
    }

    public Long getOwnerbyStudyId(Long studyId) {
        return userStudyRepository.findOwnerByStudyId(studyId);
    }
    public boolean isStudyOwner(Long studyId, User user) {
        return user.getId().equals(getOwnerbyStudyId(studyId));
    }

    public List<UserStudy> getAllNotExitedUsersByStudyId(Long studyId){
        List<UserStudy> findUsers = userStudyRepository.findAllNotExitedUsersBySyudyId(studyId);
        return findUsers;
    }

    @Transactional
    public ResponseEntity<String> getMemberOutOfStudy(Long studyId, Long userId, List<String> exitReason, String bearerToken) {
        //user_study update
        UserStudy findUserStudy = userStudyRepository.findById(new UserStudyId(userId, studyId)).orElse(null);
        if(findUserStudy == null){
            return ResponseEntity.status(404).body("존재하지 않는 유저");
        }
        ExitStatus exitStatus = findUserStudy.getExit_status();
        //이미 강퇴된 유저는 강퇴 안하고 끝내기
        if(exitStatus==ExitStatus.Leave || exitStatus==ExitStatus.Forced_leave){ //내보내진 유저
            return ResponseEntity.status(404).body("내보내진 유저");
        }
        Study findSutdy = studyRepository.findById(studyId).orElse(null);
        if(findSutdy != null && exitStatus==ExitStatus.None) {
            findUserStudy.updateExitStatus(ExitStatus.Forced_leave); //강퇴
            findSutdy.updateCurParticipants("-", 1);

            //exit_reasons 저장
            exitReason.forEach(reason -> {
                        ExitReason.Element er = ExitReason.toExitResaon(reason);
                        ExitReasons findExitReason = exitReasonsRepository.findByReason(er);

                        ExitReasons build = ExitReasons.builder()
                                .reason(er)
                                .build();

                        if (findExitReason == null) { //아직 없는 이유이면, 이유 저장
                            exitReasonsRepository.save(build);
                            findExitReason = exitReasonsRepository.findByReason(er);
                        }
                        System.out.println("build.getId() = " + build.getId());

                        if (findUserStudy != null && findExitReason!=null) { //탈퇴 이유 리스트에 저장
                            UserStudyExit userStudyExit = UserStudyExit.builder()
                                    .id(new UserStudyExitId(new UserStudyId(userId, studyId), findExitReason.getId()))
                                    .userStudy(findUserStudy)
                                    .exitReasons(findExitReason)
                                    .exit_date(LocalDateTime.now())
                                    .build();
                            userStudyExitRepository.save(userStudyExit);
                        }
                    });

            //채팅 서버에서 유저 삭제
            DefaultResponse defaultResponse = chatServerService.deleteStudyMember(
                    PostStudyDeleteMemberRequest.builder()
                            .token(bearerToken)
                            .studyId(studyId)
                            .userId(userId)
                            .build()
            );
            System.out.println("defaultResponse = " + defaultResponse);
        }
        return ResponseEntity.status(200).body("스터디 멤버 내보내기 성공");
    }

}
