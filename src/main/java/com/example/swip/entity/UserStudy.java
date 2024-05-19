package com.example.swip.entity;

import com.example.swip.entity.compositeKey.UserStudyId;
import com.example.swip.entity.enumtype.ExitReason;
import com.example.swip.entity.enumtype.ExitStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserStudy {
    @EmbeddedId
    @Column(name = "user_study_id")
    private UserStudyId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("studyId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    private boolean is_owner;
    private ExitStatus exit_status;
    private LocalDateTime join_date; //참여 날짜

    @OneToMany(mappedBy = "userStudy", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserStudyExit> userStudyExits = new ArrayList<>();

    public void updateExitStatus(ExitStatus exit_status){
        this.exit_status = exit_status;
    }
    public void setIs_owner(boolean is_owner) {
        this.is_owner = is_owner;
    }
}
