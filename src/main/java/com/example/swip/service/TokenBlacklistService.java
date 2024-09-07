package com.example.swip.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final ConcurrentHashMap<String, Boolean> blacklist = new ConcurrentHashMap<>();

    public void addTokenToBlacklist(String token) {
        blacklist.put(token, Boolean.TRUE);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklist.containsKey(token);
    }
}
