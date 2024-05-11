package com.example.swip.config;

import com.example.swip.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DailyCleanupTask {
    private final UserService userService;

    public DailyCleanupTask(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupActive() {
        userService.deleteExpiredUserData(LocalDateTime.now());
    }
}
