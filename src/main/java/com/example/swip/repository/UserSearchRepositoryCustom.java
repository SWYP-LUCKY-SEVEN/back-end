package com.example.swip.repository;

import com.example.swip.entity.UserSearch;

import java.time.LocalDateTime;
import java.util.List;

public interface UserSearchRepositoryCustom {

    long deleteAllByUserId(Long userId);

    List<UserSearch> findSearchById(Long userId);

    void deleteExpiredSearch(LocalDateTime time);

    List<UserSearch> findExpiredSearch(LocalDateTime time);
}
