package com.example.swip.entity;

import com.example.swip.entity.compositeKey.UserSearchId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserSearch {
    @EmbeddedId
    private UserSearchId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("searchId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "search_id")
    private Search search;

    private int count; //검색 횟수

    @UpdateTimestamp(source = SourceType.DB)
    private LocalDateTime update_time; //수정 시간

    public void updateLog() {
        this.count = this.count + 1;
    }
}
