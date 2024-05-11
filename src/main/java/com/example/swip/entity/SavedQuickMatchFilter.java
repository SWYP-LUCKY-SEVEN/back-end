package com.example.swip.entity;

import com.example.swip.entity.enumtype.Tendency;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class SavedQuickMatchFilter {
    @Id
    private Long id;
  
    private LocalDate start_date; //시작 날짜
  
    private String duration;    //진행 기간
    private Long tendency; //스터디 성향
    private Long min_member;
    private Long max_member;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

}
