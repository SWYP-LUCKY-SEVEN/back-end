package com.example.swip.entity;

import com.example.swip.entity.enumtype.MatchingType;
import com.example.swip.entity.enumtype.StudyProgressStatus;
import com.example.swip.entity.enumtype.Tendency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    private StudyProgressStatus.Element status; //스터디 진행 상태 - enum?

    private boolean recruit_status; //스터디 모집 상태 - false: 모집완료, true: 모집중

    private MatchingType.Element matching_type;

    private int view_count; //조회수

    @ManyToOne(fetch = FetchType.LAZY, optional = false) //not null
    @JoinColumn(name = "category_id")
    private Category category;

    @CreationTimestamp(source = SourceType.DB)
    private LocalDateTime created_time;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    @Builder.Default
    private List<StudyTodoPublic> todos = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AdditionalInfo> additionalInfos = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserStudy> userStudies = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    @Builder.Default
    private List<JoinRequest> joinRequests = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FavoriteStudy> favoriteStudies = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    @Builder.Default
    private List<StudyTodoPublic> studyTodoPublics = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    @Builder.Default
    private List<StudyTodo> studyTodos = new ArrayList<>();

    public void updateCurParticipants(String sign, int num){
        if(Objects.equals(sign, "+")){
            this.cur_participants_num = this.cur_participants_num + num;
        } else if (Objects.equals(sign, "-")) {
            this.cur_participants_num = this.cur_participants_num - num;
        }

    }

    public void updateViewcount(){
        this.view_count = this.view_count + 1;
    }
    public void updateStatus(StudyProgressStatus.Element status){
        this.status = status;
    }

    public void updateStudy(Study study, String title, String description, List<String> tags) {
        this.title = title;
        this.description = description;

        // 기존의 추가 정보를 모두 삭제하고 새로운 추가 정보를 생성하여 연결
        this.additionalInfos.clear();

        List<AdditionalInfo> additionalInfoList = tags.stream().map(
                tag -> {
                    return AdditionalInfo.builder()
                            .name(tag)
                            .study(study)
                            .build();
                }
        ).collect(Collectors.toList());

        this.additionalInfos.addAll(additionalInfoList);
    }
}
