package com.example.swip.entity;

import com.example.swip.entity.enumtype.MatchingType;
import com.example.swip.entity.enumtype.StudyProgressStatus;
import com.example.swip.entity.enumtype.Tendency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    private LocalDateTime start_date; //시작 날짜
    private LocalDateTime end_date; //종료 날짜
    private String duration;

    private int max_participants_num;
    private int cur_participants_num;

    private Tendency tendency; //스터디 성향 - enum?

    private StudyProgressStatus status; //스터디 진행 상태 - enum?

    private boolean recruit_status; //스터디 모집 상태 - false: 모집완료, true: 모집중

    private MatchingType matching_type;

    private int view_count; //조회수

    @CreationTimestamp
    private LocalDateTime created_time;

    @OneToMany(mappedBy = "study")
    private List<Todo> todos = new ArrayList<>();

    @OneToMany(mappedBy = "study")
    private List<StudyCategory> studyCategories = new ArrayList<>();

    @OneToMany(mappedBy = "study")
    private List<AdditionalInfo> additionalInfos = new ArrayList<>();


    //comment entity 추가
    public void updateBoard(String title, String description){
        this.title = title;
        this.description = description;
    }

}
