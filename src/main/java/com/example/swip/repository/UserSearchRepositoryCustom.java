package com.example.swip.repository;

import com.example.swip.entity.UserSearch;
import java.util.List;

public interface UserSearchRepositoryCustom {

    long deleteAllByUserId(Long userId);

    List<UserSearch> findSearchById(Long userId);
}
