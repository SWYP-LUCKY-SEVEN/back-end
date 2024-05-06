package com.example.swip.repository;

import com.example.swip.dto.QStudyFilterResponse;
import com.example.swip.dto.StudyFilterCondition;
import com.example.swip.dto.StudyFilterResponse;
import com.example.swip.entity.QAdditionalInfo;
import com.example.swip.entity.QCategory;
import com.example.swip.entity.Study;
import com.example.swip.entity.enumtype.MatchingType;
import com.example.swip.entity.enumtype.Tendency;
import com.querydsl.core.BooleanBuilder;
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
import java.util.stream.Collectors;

import static com.example.swip.entity.QStudy.study;


public class StudyFilterRepositoryImpl implements StudyFilterRepository {

    private final JPAQueryFactory queryFactory;

    public StudyFilterRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public List<StudyFilterResponse> filterStudy(StudyFilterCondition filterCondition) {

        QCategory category = QCategory.category;

        BooleanBuilder builder = new BooleanBuilder();

        /**
         * 리스트 종류 구분 - 신규, 전체, 마감임박, 승인 없는 / 검색 결과 => path variable로 받기
         */

        if(filterCondition.getPage_type() != null){
            String pageType = filterCondition.getPage_type();
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
                            .or(recruitPercentage.gt(80)) //스터디 모집 인원이 80% 초과인 경우
                    );
                    break;
                case "nonApproval": //승인없는
                    builder.and(study.matching_type.eq(MatchingType.Quick)); //빠른 매칭 타입
                    break;
                default:
                    break;
            }
        }
        // 검색어 : 검색결과 -> title일치 or additional info 에 포함
        if(filterCondition.getQuery_string()!=null) {
            String queryString = filterCondition.getQuery_string();
            builder.and(
                    study.title.contains(queryString)
                            .or(study.additionalInfos.any().name.contains(queryString))
            );
        }

        /**
         *  필터 조건에 따라 쿼리에 조건 추가
         */
        // 빠른 매칭
        if(filterCondition.getQuick_match() != null){
            String quickMatch = filterCondition.getQuick_match();
            switch (quickMatch){
                case "빠른 매칭":
                    builder.and(study.matching_type.eq(MatchingType.Quick));
                    break;
                default:
                    break;
            }
        }
        // 카테고리 선택
        if(filterCondition.getCategories() != null && !filterCondition.getCategories().isEmpty()){
            builder.and(category.name.in(filterCondition.getCategories())); //하나라도 일치하는 것 출력.
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
        if (filterCondition.getMax_participants() != null){
            builder.and(study.max_participants_num.eq(filterCondition.getMax_participants()));
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

        JPAQuery<Study> query = queryFactory
                .selectFrom(study)
                .leftJoin(study.category, category).fetchJoin();

        List<Study> findStudy = query
                .where(builder) //필터링
                .orderBy(orderSpecifiers) //정렬
                .distinct() //중복 제거
                .fetch();

        List<StudyFilterResponse> responses = findStudy.stream()
                .map(r -> new StudyFilterResponse(
                        r.getId(),
                        r.getTitle(),
                        r.getStart_date(),
                        r.getEnd_date(),
                        r.getMax_participants_num(),
                        r.getCur_participants_num(),
                        r.getCreated_time(),
                        r.getCategory().getName(),
                        r.getAdditionalInfos().stream()
                                .map(info -> info.getName())
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return responses;
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
