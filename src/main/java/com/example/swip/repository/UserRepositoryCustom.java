package com.example.swip.repository;

import com.example.swip.entity.QCategory;
import com.example.swip.entity.QStudy;
import com.example.swip.entity.Study;
import com.example.swip.entity.UserStudy;
import com.example.swip.entity.enumtype.StudyProgressStatus;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import static com.example.swip.entity.QUserStudy.userStudy;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustom {
    // 이를 위해서는 빈으로 등록이 필요하다. => QuerydslConfiguration
    private final JPAQueryFactory queryFactory;

    public Long countProposer(Long userId) {   //신청중인 개수 카운트
        QStudy study = QStudy.study;
        return queryFactory
                .select(userStudy.count())
                .from(userStudy)
                .where(userStudy.user.id.eq(userId))
                .leftJoin(userStudy.study, study)
                .fetchJoin()
                .distinct()
                .fetchFirst();
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
}