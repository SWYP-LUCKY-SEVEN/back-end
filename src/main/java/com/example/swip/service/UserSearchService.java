package com.example.swip.service;

import com.example.swip.entity.Search;
import com.example.swip.entity.User;
import com.example.swip.entity.UserSearch;
import com.example.swip.entity.compositeKey.UserSearchId;
import com.example.swip.repository.SearchRepository;
import com.example.swip.repository.UserSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserSearchService {
    private final UserSearchRepository userSearchRepository;
    private final SearchRepository searchRepository;
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

    //user id로 유저별 최근 검색기록 조회
    public List<UserSearch> findRecentSearchByUserId(Long userId) {
        return userSearchRepository.findSearchById(userId);
    }

    //userId에 해당하는 검색어 모두 삭제
    @Transactional
    public long deleteRecentSearch(Long userId) {
        long deletedCount = userSearchRepository.deleteAllByUserId(userId);
        return deletedCount;
    }

    @Transactional
    public void deleteRecentSearch(Long userId, Long searchId) {
        userSearchRepository.deleteById(new UserSearchId(userId, searchId));
    }

    public Optional<UserSearch> findById(Long writerId, Long searchId) {
        Optional<UserSearch> findUserSearch = userSearchRepository.findById(new UserSearchId(writerId, searchId));
        return findUserSearch;
    }

    @Transactional
    public void deleteExpiredSearch(LocalDateTime time){
        //7일 넘은 기록 찾기
        List<UserSearch> expiredSearch = userSearchRepository.findExpiredSearch(time);
        //해당 갯수만큼 search table에서 count 줄이기 -> count가 0 이하이면 그냥 0으로
        expiredSearch.forEach(
                userSearch -> {
                    Optional<Search> findSearch = searchRepository.findById(userSearch.getId().getSearchId());
                    if(findSearch.isPresent()){
                        findSearch.get().reduceCount(userSearch.getCount());
                    }
                }
        );
        //7일 넘은 기록 삭제
        userSearchRepository.deleteExpiredSearch(time);
    }
}
