package com.example.swip.entity;

import com.example.swip.entity.enumtype.Tendency;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class SavedQuickMatchFilter {
    @Id
    @Column(name = "filter_id")
    private Long id;    //user의 id를
  
    private LocalDate start_date; //시작 날짜
  
    private String duration;    //진행 기간
    private Tendency.Element tendency; //스터디 성향
    private Long min_member;
    private Long max_member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


}
