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
  
    //private LocalDate start_date; //시작 날짜 // 2024-09-14 제거 요청
  
    private String duration;    //진행 기간
    private Long tendency; //스터디 성향
    private Long mem_scope;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public void updateFilter(Category category, String duration, Long tendency, Long mem_scope) {
        this.category = category;
        this.duration = duration;
        this.tendency = tendency;
        this.mem_scope = mem_scope;
    }
}
