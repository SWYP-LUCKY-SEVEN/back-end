package com.example.swip.entity;

import com.example.swip.entity.enumtype.MatchingType;
import com.example.swip.entity.enumtype.StudyProgressStatus;
import com.example.swip.entity.enumtype.Tendency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Study {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    private String title;

    private String description;

    private LocalDate start_date; //시작 날짜
    private LocalDate end_date; //종료 날짜
    private String duration;

    private int max_participants_num;
    private int cur_participants_num;

    private Tendency.Element tendency; //스터디 성향 - enum?

    private StudyProgressStatus status; //스터디 진행 상태 - enum?

    private boolean recruit_status; //스터디 모집 상태 - false: 모집완료, true: 모집중

    private MatchingType.Element matching_type;

    private int view_count; //조회수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @CreationTimestamp
    private LocalDateTime created_time;

    @OneToMany(mappedBy = "study")
    @Builder.Default
    private List<Todo> todos = new ArrayList<>();

    @OneToMany(mappedBy = "study")
    @Builder.Default
    private List<AdditionalInfo> additionalInfos = new ArrayList<>();

}
