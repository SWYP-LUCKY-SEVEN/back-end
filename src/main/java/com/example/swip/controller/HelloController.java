package com.example.swip.controller;

import com.example.swip.config.UserPrincipal;
import com.example.swip.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {
    private final S3Service s3Service;
    @GetMapping("/")
    public String greeting(){
        return "Hello, World";
    }

    @GetMapping("/secured")
    public String secured(@AuthenticationPrincipal UserPrincipal principal) {
        return "If you see this, then you're logged in as user " + principal.getEmail()
                + " User ID: " + principal.getUserId();
    }

    @GetMapping("/admin")
    public String admin(@AuthenticationPrincipal UserPrincipal principal) {
        return "If you see this, then you are an Admin. User ID: " + principal.getUserId();
    }
}