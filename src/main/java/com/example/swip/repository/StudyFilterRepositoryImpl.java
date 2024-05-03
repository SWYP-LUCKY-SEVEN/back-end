package com.example.swip.repository;

import com.example.swip.dto.QStudyFilterResponse;
import com.example.swip.dto.StudyFilterCondition;
import com.example.swip.dto.StudyFilterResponse;
import com.example.swip.entity.QAdditionalInfo;
import com.example.swip.entity.QCategory;
import com.example.swip.entity.QStudyCategory;
import com.example.swip.entity.enumtype.MatchingType;
import com.example.swip.entity.enumtype.Tendency;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.swip.entity.QStudy.study;


public class StudyFilterRepositoryImpl implements StudyFilterRepository {

    private final JPAQueryFactory queryFactory;

    public StudyFilterRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public List<StudyFilterResponse> filterStudy(StudyFilterCondition filterCondition) {
        QStudyCategory studyCategory = QStudyCategory.studyCategory;
        QCategory category = QCategory.category;
        QAdditionalInfo additionalInfo = QAdditionalInfo.additionalInfo;

        BooleanBuilder builder = new BooleanBuilder();

        JPAQuery<StudyFilterResponse> query = queryFactory
                .select(
                        new QStudyFilterResponse(
                                study.id,
                                study.title,
                                study.start_date,
                                study.end_date,
                                study.max_participants_num,
                                study.cur_participants_num,
                                study.created_time)
                )
                .from(study)
                .leftJoin(study.studyCategories, studyCategory)
                .leftJoin(studyCategory.category, category)
                .leftJoin(study.additionalInfos, additionalInfo);

        /**
         * 리스트 종류 구분 - 신규, 전체, 마감임박, 승인 없는 / 검색 결과 => path variable로 받기
         */

        if(filterCondition.getPageType() != null){
            String pageType = filterCondition.getPageType();
            switch (pageType){
                case "recent": //신규
                    builder.and(study.created_time.before(LocalDateTime.now().plusWeeks(2))); //최근 2주간 등록
                    break;
                case "all": //전체
                    break;
                case "deadline": //마감임박
                    NumberExpression<Integer> curParticipantsNum = study.cur_participants_num;
                    NumberExpression<Integer> maxParticipantsNum = study.max_participants_num;

                    NumberExpression<Integer> recruitPercentage = Expressions.numberOperation(Integer.class, Ops.DIV, curParticipantsNum, maxParticipantsNum)
                            .multiply(100);

                    builder.and(
                            study.start_date.before(LocalDateTime.now().plusDays(7)) //스터디 시작 일자가 7일 전일 경우
                            .or(recruitPercentage.gt(80))
                    );
                    //스터디 모집 인원이 80% 초과인 경우
                    break;
                case "nonApproval": //승인없는
                    builder.and(study.matching_type.eq(MatchingType.Quick)); //빠른 매칭 타입
                    break;
                default:
                    break;
            }
        }

        /**
         *  필터 조건에 따라 쿼리에 조건 추가
         */

        // 카테고리 선택
        if(filterCondition.getCategory() != null){
            builder.and(category.name.eq(filterCondition.getCategory()));
        }
        //시작 날짜만 선택 -> 시작 날짜 일치하는 것
        if (filterCondition.getStart_date() != null && filterCondition.getDuration() == null){
            builder.and(study.start_date.eq(filterCondition.getStart_date()));
        }
        //duration만 선택 -> duration 일치하는 것
        if (filterCondition.getDuration() != null && filterCondition.getStart_date() == null){
            builder.and(study.duration.eq(filterCondition.getDuration()));
        }
        //시작 날짜 & duration 지정 -> 둘다 일치하는 것
        if (filterCondition.getDuration() != null && filterCondition.getStart_date() != null){
            builder.and(study.start_date.eq(filterCondition.getStart_date()))
                            .and(study.duration.eq(filterCondition.getDuration()));
        }
        //인원 수
        //최대 인원만 존재
        if (filterCondition.getMax_participants() == null && filterCondition.getMax_participants() != null){
            builder.and(study.max_participants_num.eq(filterCondition.getMax_participants()));
        }
        //최대, 최소 인원 존재
        if (filterCondition.getMin_participants() != null && filterCondition.getMax_participants() != null){
            builder.and(study.max_participants_num.between(filterCondition.getMin_participants(), filterCondition.getMax_participants()));
        }
        //성향
        if(filterCondition.getTendency() != null){
            String tendency = filterCondition.getTendency();
            Tendency result = null;
            switch (tendency) {
                case "활발한 대화와 동기부여 원해요":
                    result = Tendency.Active;
                    break;
                case "학습 피드백을 주고 받고 싶어요":
                    result = Tendency.Feedback;
                    break;
                case "조용히 집중하고 싶어요":
                    result = Tendency.Focus;
                    break;
                default:
                    // 예상치 못한 값이 들어온 경우 처리하지 않음
                    break;
            }

            if (result != null) {
                builder.and(study.tendency.eq(result));
            }
        }

        //정렬 조건 설정
        OrderSpecifier[] orderSpecifiers = createOrderSpecifier(filterCondition.getOrder_type());

        return query
                .where(builder) //필터링
                .orderBy(orderSpecifiers) //정렬
                .distinct() //중복 제거
                .fetch();
    }

    private OrderSpecifier[] createOrderSpecifier(String orderType){
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        if(orderType != null) {
            switch (orderType) {
                case "최근 등록순":
                    orderSpecifiers.add(new OrderSpecifier(Order.DESC, study.created_time));
                    break;
                case "인기순":
                    orderSpecifiers.add(new OrderSpecifier(Order.DESC, study.view_count));
                    break;
                case "마감 임박순":
                    orderSpecifiers.add(new OrderSpecifier(Order.ASC, study.start_date));
                    break;
                case "가나다순":
                    orderSpecifiers.add(new OrderSpecifier(Order.ASC, study.title));
                    break;
                default:
                    orderSpecifiers.add(new OrderSpecifier(Order.DESC, study.created_time));
            }
        }
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

}
