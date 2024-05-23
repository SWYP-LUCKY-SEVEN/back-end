package com.example.swip.repository;

import com.example.swip.dto.UserRelationship;
import com.example.swip.dto.study.StudyFilterResponse;
import com.example.swip.dto.user.SimpleUserProfileDto;
import com.example.swip.entity.*;
import com.example.swip.entity.enumtype.ExitStatus;
import com.example.swip.entity.enumtype.StudyProgressStatus;
import com.querydsl.core.Query;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
                .join(favoriteStudy.study, study)
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
                .join(joinRequest.study, study)
                .where(joinRequest.user.id.eq(userId))
                .fetch();
    }
    public List<Study> processStudyList(Long userId) {   //InProgress 상태의 스터디 개수
        QStudy study = QStudy.study;

        BooleanExpression statusCondition = userStudy.study.status.eq(StudyProgressStatus.Element.InProgress);
        BooleanExpression userCondition = userStudy.user.id.eq(userId);
        BooleanExpression exitCondition = userStudy.exit_status.eq(ExitStatus.None);

        return queryFactory
                .select(study)
                .from(userStudy)
                .join(userStudy.study, study)
                .where(statusCondition,userCondition,exitCondition)  //탈퇴 혹은 강퇴된 스터디는 제외
                .fetch();
    }
    public List<StudyFilterResponse> registeredStudyList(Long userId, StudyProgressStatus.Element status) {   //InProgress 상태의 스터디 개수
        QStudy study = QStudy.study;
        BooleanExpression statusCondition = null;
        if(status != null)
            statusCondition = userStudy.study.status.eq(status);
        BooleanExpression userCondition = userStudy.user.id.eq(userId);
        BooleanExpression exitCondition = userStudy.exit_status.eq(ExitStatus.None);

        List<Tuple> result = queryFactory
                .select(study, userStudy.is_owner)
                .from(userStudy)
                .join(userStudy.study, study)
                .where(statusCondition,userCondition,exitCondition)  //탈퇴 혹은 강퇴된 스터디는 제외
                .fetch();

        return result.stream()
                .map(tuple -> {
                    Study s = tuple.get(study);
                    Boolean is_owner = tuple.get(userStudy.is_owner);
                    UserRelationship user_relation =
                            new UserRelationship(is_owner, true, null);
                    return new StudyFilterResponse(
                            s.getId(),
                            s.getTitle(),
                            StudyProgressStatus.toString(s.getStatus()),
                            s.getStart_date(),
                            s.getEnd_date(),
                            s.getMax_participants_num(),
                            s.getCur_participants_num(),
                            s.getCreated_time(),
                            s.getCategory().getName(),
                            s.getAdditionalInfos().stream()
                                    .map(info -> info.getName())
                                    .collect(Collectors.toList()),
                            user_relation
                            );
                        }
                ).collect(Collectors.toList());
    }
    public Long countProposer(Long userId) {   //신청중인 개수 카운트
        QJoinRequest joinRequest = QJoinRequest.joinRequest;
        QStudy study = QStudy.study;
        return queryFactory
                .select(joinRequest.count())
                .from(joinRequest)
                .join(joinRequest.study, study)
                .where(joinRequest.user.id.eq(userId))
                .fetchFirst();
    }
    public Long countFavorite(Long userId) {   //찜 중인 개수 카운트
        return queryFactory
                .select(favoriteStudy.count())
                .from(favoriteStudy)
                .where(
                        favoriteStudy.user.id.eq(userId),
                        favoriteStudy.study.status.ne(StudyProgressStatus.Element.Done)
                ).fetchOne();
    }
    public Long countInUserStudy(Long userId, StudyProgressStatus.Element status) {   //InProgress 상태의 스터디 개수
        Long test = queryFactory
                .select(userStudy.count())
                .from(userStudy)
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