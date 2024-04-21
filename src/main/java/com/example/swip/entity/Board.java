package com.example.swip.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String title;
    private String content;

    // 회원 정보 entity 생성 후 연결 필요
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    @CreationTimestamp
    private LocalDateTime created_time;

    @UpdateTimestamp
    private LocalDateTime updated_time;

    //comment entity 추가

    public void updateBoard(String title, String content){
        this.title = title;
        this.content = content;
    }

}
