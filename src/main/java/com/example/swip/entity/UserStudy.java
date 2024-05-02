package com.example.swip.entity;

import com.example.swip.entity.compositeKey.UserStudyId;
import com.example.swip.entity.enumtype.ExitReason;
import com.example.swip.entity.enumtype.ExitStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserStudy {
    @EmbeddedId
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
    private ExitReason exit_reason;
}
