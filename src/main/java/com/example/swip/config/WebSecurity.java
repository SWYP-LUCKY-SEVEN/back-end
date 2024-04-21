package com.example.swip.config;

import org.springframework.security.core.Authentication;

public class WebSecurity {
    public boolean checkUserHasAccessToProjectId(Authentication authentication, int projectId) {
        // here you can check if the user has the correct role
        // or implement more complex and custom authorization logic if necessary
        System.out.println(authentication);
        System.out.println(projectId);
        return false;
    }
}