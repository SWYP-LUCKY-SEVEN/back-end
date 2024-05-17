package com.example.swip.repository;

import com.example.swip.entity.JoinRequest;
import com.example.swip.entity.enumtype.JoinStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.swip.entity.QJoinRequest.joinRequest;
import static com.example.swip.entity.QUser.user;

public class JoinRequestRepositoryImpl implements JoinRequestRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public JoinRequestRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<JoinRequest> findAllByStudyId(Long studyId) {
        List<JoinRequest> fetch = queryFactory
                .select(joinRequest)
                .from(joinRequest)
                .leftJoin(joinRequest.user, user)
                .fetchJoin()
                .where(joinRequest.id.studyId.eq(studyId))
                .orderBy(joinRequest.request_date.desc())
                .fetch();
        return fetch;
    }

    @Override
    public List<JoinRequest> findAllWaitingByStudyId(Long studyId) {
        List<JoinRequest> fetch = queryFactory
                .select(joinRequest)
                .from(joinRequest)
                .leftJoin(joinRequest.user, user)
                .fetchJoin()
                .where(
                        joinRequest.id.studyId.eq(studyId),
                        joinRequest.join_status.eq(JoinStatus.Waiting)
                )
                .fetch();
        return fetch;
    }

    @Override
    public void deleteExpiredJoinRequest(LocalDateTime time) {
        queryFactory
                .delete(joinRequest)
                .where(joinRequest.request_date.before(time.minusDays(3)))
                .execute();
    }

}
