package com.example.swip.repository.impl;

import com.example.swip.entity.*;
import com.example.swip.repository.custom.StudyRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static com.example.swip.entity.QAdditionalInfo.additionalInfo;
import static com.example.swip.entity.QCategory.category;
import static com.example.swip.entity.QStudy.study;

public class StudyRespositoryImpl implements StudyRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    public StudyRespositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public Study findStudyDetailById(Long studyId) {
        //study
        Study studyDetail = queryFactory
                .select(study)
                .from(study)
                .leftJoin(study.category, category)
                .leftJoin(study.additionalInfos, additionalInfo)
                .where(study.id.eq(studyId))
                .fetchOne();

        return studyDetail;
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
