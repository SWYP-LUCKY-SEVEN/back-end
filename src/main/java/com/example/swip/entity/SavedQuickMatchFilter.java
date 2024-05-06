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
    private List<Long> mem_scope;  //스터디 멤버 수

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


}
