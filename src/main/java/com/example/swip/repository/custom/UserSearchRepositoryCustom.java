package com.example.swip.repository.custom;

import com.example.swip.entity.UserSearch;

import java.time.LocalDateTime;
import java.util.List;

public interface UserSearchRepositoryCustom {

    long deleteAllByUserId(Long userId);

    List<UserSearch> findRecent10SearchById(Long userId);
}
