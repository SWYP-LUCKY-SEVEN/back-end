package com.example.swip.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "Users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String role;

    private String email;
    private String validate;
  
    private String nickname;

    private String profile_image;
  
    private LocalDateTime join_date;
  
    private LocalDateTime withdrawal_date;

    public void updateProfile(String nickname, String profile_image){
        this.profile_image = profile_image;
        this.nickname = nickname;
        this.join_date = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Evaluation> evaluations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<UserStudy> userStudies = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<FavoriteStudy> favoriteStudies = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<JoinRequest> joinRequests = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private SavedQuickMatchFilter savedQuickMatchFilter;

    //기존 error 때문에 넣어둔 field => 추후 삭제 요망 (현재는 테스트 코드 용으로 사용중)
    private String password;
}