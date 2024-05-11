package com.example.swip.repository;

import com.example.swip.entity.*;
import com.example.swip.entity.enumtype.StudyProgressStatus;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import static com.example.swip.entity.QUserStudy.userStudy;
import static com.example.swip.entity.QFavoriteStudy.favoriteStudy;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustom {
    // 이를 위해서는 빈으로 등록이 필요하다. => QuerydslConfiguration
    private final JPAQueryFactory queryFactory;

    public List<Study> favoriteStudyList(Long userId) {
        QStudy study = QStudy.study;
        List<Study> findStudy = queryFactory
                .select(favoriteStudy.study)
                .from(favoriteStudy)
                .innerJoin(study)
                .on(favoriteStudy.study.eq(study))
                .fetchJoin()
                .where(favoriteStudy.user.id.eq(userId))
                .fetch();
        return findStudy;
    }
    public Long countProposer(Long userId) {   //신청중인 개수 카운트
        QJoinRequest joinRequest = QJoinRequest.joinRequest;
        QStudy study = QStudy.study;
        return queryFactory
                .select(joinRequest.count())
                .from(joinRequest)
                .innerJoin(study)
                .on(joinRequest.study.eq(study))
                .fetchJoin()
                .where(joinRequest.user.id.eq(userId))
                .fetchFirst();
    }
    public Long countFavorite(Long userId) {   //신청중인 개수 카운트
        QStudy study = QStudy.study;
        return queryFactory
                .select(favoriteStudy.count())
                .from(favoriteStudy)
                .innerJoin(study)
                .on(favoriteStudy.study.eq(study))
                .fetchJoin()
                .where(
                        favoriteStudy.user.id.eq(userId),
                        favoriteStudy.study.status.ne(StudyProgressStatus.Done)
                ).fetchOne();
    }
    public Long countInUserStudy(Long userId, boolean isComplete) {   //InProgress 상태의 스터디 개수
        QStudy study = QStudy.study;
        BooleanExpression be = null;
        if(isComplete)
            be = userStudy.study.status.eq(StudyProgressStatus.Done);
        else
            be = userStudy.study.status.ne(StudyProgressStatus.Done);
        Long test = queryFactory
                .select(userStudy.count())
                .from(userStudy)
                .innerJoin(study)
                .on(userStudy.study.eq(study))
                .fetchJoin()
                .where(userStudy.user.id.eq(userId),be)
                .fetchOne();
        System.out.println(test);
        return test;
    }

    public void deleteExpiredUserData(LocalDateTime time) {
        QUser user = QUser.user;
        queryFactory.delete(user).where(user.withdrawal_date.before(time));
    }
}