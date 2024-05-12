package com.example.swip.entity;

import com.example.swip.entity.enumtype.ExitReason;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ExitReasons {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exit_reasons_id")
    private Long id;

    private ExitReason.Element reason;
}
