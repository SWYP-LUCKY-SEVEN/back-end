package com.example.swip.config;

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

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupActive() {
        userService.deleteExpiredUserData(LocalDateTime.now());
        studyService.progressStartStudy(LocalDate.now());
        studyService.completeExpiredStudy(LocalDate.now());
    }
}
