package com.example.swip.service;

import com.example.swip.entity.Search;
import com.example.swip.entity.User;
import com.example.swip.entity.UserSearch;
import com.example.swip.entity.compositeKey.UserSearchId;
import com.example.swip.repository.UserSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserSearchService {
    private final UserSearchRepository userSearchRepository;
    @Transactional
    public UserSearchId saveSearchLog(Search findKeyword, User user) {
        UserSearch userSearch = UserSearch.builder()
                .id(new UserSearchId(user.getId(), findKeyword.getId()))
                .user(user)
                .search(findKeyword)
                .count(1)
                .build();
        UserSearch savedSearchLog = userSearchRepository.save(userSearch);
        return savedSearchLog.getId();
    }

    @Transactional
    public void updateSearchLog(Search findKeyword, User user) {
        UserSearchId id = new UserSearchId(user.getId(), findKeyword.getId());
        UserSearch findUserSearch = userSearchRepository.findById(id).orElse(null);
        findUserSearch.updateLog(); //count +1
    }
}
