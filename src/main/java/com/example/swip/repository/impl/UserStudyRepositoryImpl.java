package com.example.swip.repository.impl;
import com.example.swip.dto.UserRelationship;
import com.example.swip.entity.QFavoriteStudy;
import com.example.swip.entity.QUserStudy;
import com.example.swip.entity.UserStudy;
import com.example.swip.entity.compositeKey.UserStudyId;
import com.example.swip.entity.enumtype.ChatStatus;
import com.example.swip.entity.enumtype.ExitStatus;
import com.example.swip.repository.custom.UserStudyRepositoryCustom;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
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
    public List<UserStudy> findStudyByUserId(Long userId) {
        return queryFactory
                .select(userStudy)
                .from(userStudy)
                .where(userStudy.id.userId.eq(userId))
                .fetch();
    }

    @Override
    public List<UserStudy> findStudyByStudyIdExceptOwner(Long studyId) {
        return queryFactory
                .select(userStudy)
                .from(userStudy)
                .where(userStudy.id.studyId.eq(studyId), userStudy.is_owner.eq(false))
                .fetch();
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

    @Override
    public List<UserStudy> findAllByStudyId(Long studyId) {
        List<UserStudy> findAllUsers = queryFactory
                .select(userStudy)
                .from(userStudy)
                .leftJoin(userStudy.user, user).fetchJoin()
                .where(userStudy.id.studyId.eq(studyId))
                .orderBy(userStudy.join_date.asc()) //가입한 순서대로
                .fetch();

        return findAllUsers;
    }

    @Override
    public UserStudy findUserStudyById(UserStudyId userStudyId) {
        return queryFactory
                .select(userStudy)
                .from(userStudy)
                .where(userStudy.id.eq(userStudyId))
                .fetchOne();
    }

    @Override
    public UserRelationship findRelationByUserIdAndStudyId(Long userId, Long studyId) {
        QUserStudy userStudy = QUserStudy.userStudy;
        QFavoriteStudy favoriteStudy = QFavoriteStudy.favoriteStudy;

        Tuple findStudyRelation = queryFactory
                .select(userStudy.exit_status, userStudy.is_owner)
                .from(userStudy)
                .where(userStudy.id.userId.eq(userId)
                        .and(userStudy.id.studyId.eq(studyId)))
                .fetchOne();

        Boolean favoriteStudyExist= queryFactory
                .select(favoriteStudy.isNotNull())
                .from(favoriteStudy)
                .where(favoriteStudy.id.userId.eq(userId)
                        .and(favoriteStudy.id.studyId.eq(studyId)))
                .fetchOne() != null;

        if (findStudyRelation == null) {
            return new UserRelationship(
                    false,
                    false,
                    favoriteStudyExist
            );
        }

        ExitStatus exitStatus = findStudyRelation.get(userStudy.exit_status);
        Boolean isMember = ExitStatus.valueOf("None").equals(exitStatus);
        return new UserRelationship(
                findStudyRelation.get(userStudy.is_owner),
                isMember, // userStudy is Notnull && exitStatus is None Stat
                favoriteStudyExist
        );

    }
}
