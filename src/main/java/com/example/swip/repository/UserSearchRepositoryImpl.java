package com.example.swip.repository;

import com.example.swip.entity.UserSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
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
    public List<UserSearch> findRecent10SearchById(Long userId) {
        List<UserSearch> response = queryFactory
                .selectFrom(userSearch)
                .leftJoin(userSearch.search, search).fetchJoin()
                .where(userSearch.id.userId.eq(userId))
                .orderBy(userSearch.update_time.desc())
                .limit(10)
                .fetch();
        return response;
    }

}
