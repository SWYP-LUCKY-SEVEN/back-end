package com.example.swip.repository;
import com.example.swip.entity.UserStudy;
import com.example.swip.entity.enumtype.ExitStatus;
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
    public List<UserStudy> findAllExistUsersByStudyId(Long studyId) {
        List<UserStudy> findAllUsers = queryFactory
                .select(userStudy)
                .from(userStudy)
                .leftJoin(userStudy.user, user).fetchJoin()
                .where(
                        userStudy.id.studyId.eq(studyId),
                        userStudy.exit_status.eq(ExitStatus.None)
                )
                .orderBy(userStudy.join_date.asc()) //가입한 순서대로
                .fetch();

        return findAllUsers;
    }

    @Override
    public Long findOwnerByStudyId(Long studyId) {
        return queryFactory
                .select(userStudy.id.userId)
                .from(userStudy)
                .where(
                        userStudy.id.studyId.eq(studyId),
                        userStudy.is_owner.eq(true)
                )
                .fetchOne();
    }

    @Override
    public List<UserStudy> findAllNotExitedUsersBySyudyId(Long studyId) {
        List<UserStudy> findAllUsers = queryFactory
                .select(userStudy)
                .from(userStudy)
                .leftJoin(userStudy.user, user).fetchJoin()
                .where(
                        userStudy.id.studyId.eq(studyId),
                        userStudy.exit_status.eq(ExitStatus.None),
                        userStudy.is_owner.eq(false) //방장 제외하고 출력
                )
                .orderBy(userStudy.join_date.desc())
                .fetch();

        return findAllUsers;
    }
}
