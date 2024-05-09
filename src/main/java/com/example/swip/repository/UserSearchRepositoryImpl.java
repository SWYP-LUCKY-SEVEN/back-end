package com.example.swip.repository;

import com.example.swip.entity.UserSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.example.swip.entity.QSearch.search;
import static com.example.swip.entity.QUserSearch.userSearch;

public class UserSearchRepositoryImpl implements UserSearchRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public UserSearchRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public long deleteAllByUserId(Long userId) {
        long execute = queryFactory
                .delete(userSearch)
                .where(userSearch.id.userId.eq(userId))
                .execute();
        return execute;
    }

    @Override
    public List<UserSearch> findSearchById(Long userId) {
        //("SELECT us FROM UserSearch us JOIN FETCH us.search s WHERE us.id.userId = :userId ORDER BY us.update_time DESC ") // default: 최근 검색순 정렬.
        List<UserSearch> response = queryFactory
                .selectFrom(userSearch)
                .leftJoin(userSearch.search, search).fetchJoin()
                .where(userSearch.id.userId.eq(userId))
                .orderBy(userSearch.update_time.desc())
                .fetch();
        return response;
    }
}
