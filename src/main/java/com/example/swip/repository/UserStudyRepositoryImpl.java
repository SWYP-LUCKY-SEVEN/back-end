package com.example.swip.repository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.example.swip.entity.QUser.user;
import static com.example.swip.entity.QUserStudy.userStudy;

public class UserStudyRepositoryImpl implements UserStudyRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    public UserStudyRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<Tuple> findAllUsersByStudyId(Long studyId) {
        List<Tuple> findAllUsers = queryFactory
                .select(userStudy, user)
                .from(userStudy)
                .leftJoin(userStudy.user, user).fetchJoin()
                .where(userStudy.id.studyId.eq(studyId))
                .fetch();

        return findAllUsers;
    }
}
