package com.example.swip.entity;

import com.example.swip.entity.compositeKey.UserStudyExitId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserStudyExit {
    @EmbeddedId
    private UserStudyExitId id;

    @MapsId("userStudyId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
            value = {
                    @JoinColumn(name = "study_id", referencedColumnName = "study_id"),
                    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
            },
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT)
    )
    private UserStudy userStudy;

    @MapsId("exitReasonsId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exit_reasons_id", referencedColumnName = "exit_reasons_id")
    private ExitReasons exitReasons;

    private LocalDateTime exit_date;
}
