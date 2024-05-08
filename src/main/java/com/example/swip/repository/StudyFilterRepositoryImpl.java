package com.example.swip.repository;

import com.example.swip.dto.*;
import com.example.swip.dto.quick_match.QuickMatchFilter;
import com.example.swip.dto.quick_match.QuickMatchResponse;
import com.example.swip.entity.QAdditionalInfo;
import com.example.swip.entity.QCategory;
import com.example.swip.entity.Study;
import com.example.swip.entity.enumtype.MatchingType;
import com.example.swip.entity.enumtype.Tendency;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
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
                            study.start_date.before(LocalDate.now().plusDays(7)) //스터디 시작 일자가 7일 전일 경우
                            .or(recruitPercentage.gt(80)) //스터디 모집 인원이 80% 초과인 경우
                    );
                    break;
                case "nonApproval": //승인없는
                    builder.and(study.matching_type.eq(MatchingType.Element.Quick)); //빠른 매칭 타입
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
                case "quick":
                    builder.and(study.matching_type.eq(MatchingType.Element.Quick));
                    break;
                default:
                    break;
            }
        }
        // 카테고리 선택
        if(filterCondition.getCategory() != null){
            builder.and(category.name.eq(filterCondition.getCategory())); //일치하는 것 출력.
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
        if (filterCondition.getMax_participants() != null && filterCondition.getMin_participants()==null){
            builder.and(study.max_participants_num.between(2, filterCondition.getMax_participants()));
        }
        //최소 인원만 존재
        if(filterCondition.getMin_participants() != null && filterCondition.getMax_participants() == null){
            builder.and(study.max_participants_num.between(filterCondition.getMin_participants(), 20));
        }
        //최소&최대 인원 존재 -> 사이
        if(filterCondition.getMin_participants() != null && filterCondition.getMax_participants()!=null){
            builder.and(study.max_participants_num.between(filterCondition.getMin_participants(),filterCondition.getMax_participants()));
        }

        //성향
        if(filterCondition.getTendency()!=null) {
            builder.and(study.tendency.in(toTendencyList(filterCondition.getTendency())));
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
                case "recent":
                    orderSpecifiers.add(new OrderSpecifier(Order.DESC, study.created_time));
                    break;
                case "popular":
                    orderSpecifiers.add(new OrderSpecifier(Order.DESC, study.view_count));
                    break;
                case "deadline":
                    orderSpecifiers.add(new OrderSpecifier(Order.ASC, study.start_date));
                    break;
                case "abc":
                    orderSpecifiers.add(new OrderSpecifier(Order.ASC, study.title));
                    break;
                default:
                    orderSpecifiers.add(new OrderSpecifier(Order.DESC, study.created_time));
            }
        }
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

    private List<Tendency.Element> toTendencyList(List<String> tendency){
        if(tendency != null){
            List<Tendency.Element> tendencyList = new ArrayList<>();

            for (String t : tendency) {
                System.out.println("t = " + t);
                Tendency.Element result = Tendency.toTendency(t);
                tendencyList.add(result);
            }
            System.out.println("tendencyList = " + tendencyList);
            return tendencyList;
        }
        return null;
    }

    @Override
    public List<QuickMatchResponse> quickFilterStudy(QuickMatchFilter quickMatchFilter, Long page) {
        QCategory category = QCategory.category;

        // 카테고리 정렬
        NumberExpression<Integer> categoryRank = quickMatchFilter.getStart_date() != null ?
                new CaseBuilder()
                        .when(study.start_date.eq(quickMatchFilter.getStart_date())).then(1)
                        .otherwise(2)
                : null ;  //나머지는 2로 취급;

        // 시작일 정렬
        NumberExpression<Integer> startDateRank = quickMatchFilter.getDuration() != null ?
                new CaseBuilder()
                        .when(study.start_date.eq(quickMatchFilter.getStart_date())).then(1)
                        .otherwise(2)
                :null;

        // 진행 기간 정렬
        NumberExpression<Integer> durationRank = quickMatchFilter.getCategory() != null ?
                new CaseBuilder()
                        .when(study.category.name.eq(quickMatchFilter.getCategory())).then(1)
                        .otherwise(2)
                :null;

        // 성향 정렬
        NumberExpression<Integer> tendencyRank = quickMatchFilter.getTendency() != null ?
                new CaseBuilder()
                        .when(eqTendency(quickMatchFilter.getTendency())).then(1)
                        .otherwise(2)
                :null;

        // 인원 정렬
        Long min = quickMatchFilter.getMin_member() != null ? quickMatchFilter.getMin_member() : 0L;
        Long max = quickMatchFilter.getMax_member() != null ? quickMatchFilter.getMax_member() : 20L;
        NumberExpression<Integer> memberRank = new CaseBuilder()
                .when(study.max_participants_num.between(min, max)).then(1)
                .otherwise(2);


        // 분야 > 시작일 > 진행기간 > 성향 > 인원
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        //우선순위는 어떻게 이뤄질까?
        orderSpecifiers.add(memberRank.asc());    //성향
        if(tendencyRank != null) orderSpecifiers.add(tendencyRank.asc());    //성향
        if(durationRank != null) orderSpecifiers.add(durationRank.asc());    //진행기간
        if(startDateRank != null) orderSpecifiers.add(startDateRank.asc());   //시작일
        if(categoryRank != null) orderSpecifiers.add(categoryRank.asc());    //분야

        JPAQuery<Study> query = queryFactory
                .selectFrom(study)
                .leftJoin(study.category, category)
                .fetchJoin();

        List<Study> findStudy = query
                .where(quickFilter(quickMatchFilter))   //첫 BooleanExpression는 무조건 AND 연산이 적용된다.
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]))
                .distinct()
                .offset(page*3)  //반환 시작 index 0, 3, 6
                .limit(3)   //최대 조회 건수
                .fetch();

        List<QuickMatchResponse> responses = findStudy.stream()
                .map(study -> new QuickMatchResponse(
                        study.getId(),
                        study.getTitle(),
                        study.getCategory().getName(),
                        study.getStart_date(),
                        study.getDuration(),
                        study.getMax_participants_num(),
                        study.getCur_participants_num(),
                        study.getCreated_time()
                ))
                .collect(Collectors.toList());

        return responses;
    }
    private BooleanExpression quickFilter(QuickMatchFilter quickMatchFilter) {
        List<BooleanExpression> beList = new ArrayList<>();
        beList.add(eqStart_date(quickMatchFilter.getStart_date()));
        beList.add(eqDuration(quickMatchFilter.getDuration()));
        beList.add(eqCategory(quickMatchFilter.getCategory()));
        beList.add(eqTendency(quickMatchFilter.getTendency()));

        BooleanExpression temp = study.matching_type.eq(MatchingType.Element.Quick);
        for (BooleanExpression be : beList) {
            if(be != null) {
                temp.or(be);
            }
        }
        return temp;
    }

    private BooleanExpression eqStart_date(LocalDate startDate) {
        return startDate != null ? study.start_date.eq(startDate) : null;
    }
    private BooleanExpression eqDuration(String duration) {
        return duration != null ? study.duration.eq(duration) : null;
    }
    private BooleanExpression eqCategory(String category) {
        return category != null ? study.category.name.eq(category) : null;
    }

    private BooleanExpression eqTendency(String tendency){
        if(tendency != null){
            Tendency.Element result = null;
            switch (tendency) {
                case "active":    //active
                    result = Tendency.Element.Active;
                    break;
                case "feedback":   //feedback
                    result = Tendency.Element.Feedback;
                    break;
                case "focus":        //focus
                    result = Tendency.Element.Focus;
                    break;
                default:
                    // 예상치 못한 값이 들어온 경우 처리하지 않음
                    break;
            }
            if (result != null) {
                return study.tendency.eq(result);
            }
        }
        return null;
    }
}


