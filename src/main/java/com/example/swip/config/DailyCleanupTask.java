package com.example.swip.config;

import com.example.swip.service.JoinRequestService;
import com.example.swip.service.UserSearchService;
import com.example.swip.service.UserService;
import com.example.swip.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DailyCleanupTask {
    private final UserService userService;
    private final StudyService studyService;
    private final UserSearchService userSearchService;
    private final JoinRequestService joinRequestService;


    //@Scheduled(cron = "0 0 0 * * *")
    @Scheduled(fixedRate = 5000)
    public void cleanupActive() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        userService.deleteExpiredUserData(now);
        studyService.progressStartStudy(today);
        studyService.completeExpiredStudy(today);
        //검색어 - 7일 후 만료
        userSearchService.deleteExpiredSearch(now);
        //스터디 참가 신청 - 3일 후 만료
        joinRequestService.deleteExpiredJoinRequest(now);

    }
}
