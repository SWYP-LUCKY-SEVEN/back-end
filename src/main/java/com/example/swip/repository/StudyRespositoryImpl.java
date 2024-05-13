package com.example.swip.repository;

import com.example.swip.entity.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.example.swip.entity.QAdditionalInfo.additionalInfo;
import static com.example.swip.entity.QCategory.category;
import static com.example.swip.entity.QStudy.study;

public class StudyRespositoryImpl implements StudyRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public StudyRespositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<Study> findStudyDetailById(Long studyId) {
        //study
        List<Study> studyDetails = queryFactory
                .select(study)
                .from(study)
                .leftJoin(study.category, category)
                .leftJoin(study.additionalInfos, additionalInfo)
                .where(study.id.eq(studyId))
                .fetch();

        return studyDetails;
    }

    @Override
    public Study findStudyEditDetailById(Long studyId) {
        return queryFactory
                .selectFrom(study)
                .leftJoin(study.additionalInfos, additionalInfo)
                .where(study.id.eq(studyId))
                .fetchOne();
    }
}
