package com.example.swip.repository;

import com.example.swip.dto.user.SimpleUserProfileDto;
import com.example.swip.entity.*;
import com.example.swip.entity.enumtype.ExitStatus;
import com.example.swip.entity.enumtype.StudyProgressStatus;
import com.querydsl.core.Query;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
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
    public List<Study> proposerStudyList(Long userId) {   //신청중인 개수 카운트
        QJoinRequest joinRequest = QJoinRequest.joinRequest;
        QStudy study = QStudy.study;
        return queryFactory
                .select(study)
                .from(joinRequest)
                .innerJoin(study)
                .on(joinRequest.study.eq(study))
                .fetchJoin()
                .where(joinRequest.user.id.eq(userId))
                .fetch();
    }
    public List<Study> joinedButBeforeStartList(Long userId) {
        QStudy study = QStudy.study;
        return queryFactory
                .select(study)
                .from(userStudy)
                .innerJoin(study)
                .on(userStudy.study.eq(study))
                .fetchJoin()
                .where(userStudy.user.id.eq(userId),
                        userStudy.study.status.eq(StudyProgressStatus.Element.BeforeStart),
                        userStudy.exit_status.eq(ExitStatus.None))  //탈퇴 혹은 강퇴된 스터디는 제외
                .fetch();
    }

    public List<Study> registeredStudyList(Long userId, StudyProgressStatus.Element status) {   //InProgress 상태의 스터디 개수
        QStudy study = QStudy.study;
        BooleanExpression be;
        if(status != null)
            be = userStudy.study.status.eq(status);
        else
            be = userStudy.study.status.eq(StudyProgressStatus.Element.InProgress);

        return queryFactory
                .select(study)
                .from(userStudy)
                .innerJoin(study)
                .on(userStudy.study.eq(study))
                .fetchJoin()
                .where(userStudy.user.id.eq(userId),be,
                        userStudy.exit_status.eq(ExitStatus.None))  //탈퇴 혹은 강퇴된 스터디는 제외
                .fetch();
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
    public Long countFavorite(Long userId) {   //찜 중인 개수 카운트
        return queryFactory
                .select(favoriteStudy.count())
                .from(favoriteStudy)
                .fetchJoin()
                .where(
                        favoriteStudy.user.id.eq(userId),
                        favoriteStudy.study.status.ne(StudyProgressStatus.Element.Done)
                ).fetchOne();
    }
    public Long countInUserStudy(Long userId, StudyProgressStatus.Element status) {   //InProgress 상태의 스터디 개수
        Long test = queryFactory
                .select(userStudy.count())
                .from(userStudy)
                .fetchJoin()
                .where(userStudy.user.id.eq(userId),
                        userStudy.study.status.eq(status),
                        userStudy.exit_status.eq(ExitStatus.None))  //탈퇴 혹은 강퇴된 스터디는 제외
                .fetchOne();
        System.out.println(test);
        return test;
    }
    public List<Integer> getUserEvalList(Long userId) {
        QEvaluation evaluation = QEvaluation.evaluation;
        return queryFactory.select(evaluation.rating)
                .from(evaluation)
                .where(evaluation.to_user.id.eq(userId))
                .fetch();
    }

    public void deleteExpiredUserData(LocalDateTime time) {
        QUser user = QUser.user;
        queryFactory.delete(user).where(user.withdrawal_date.before(time));
    }
}