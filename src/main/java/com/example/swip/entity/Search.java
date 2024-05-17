package com.example.swip.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Search {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_id")
    private Long id;

    private String keyword; //검색어
    private Long count; //총 검색 횟수

    public void updateCount() {
        this.count = this.count + 1;
    }
    public void reduceCount(int count){
        if(count >=this.count){
            this.count = 0L;
        }
        else {
            this.count -= count;
        }
    }
}
