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
@NoArgsConstructor
@Builder
@Table(name = "Users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long Id;

    private String email;
  
    private String nickname;

    private String profileImage;
  
    private LocalDateTime joinDate;
  
    private LocalDateTime withdrawalDate;

//    @PrePersist
//    public void onPrePersist() {
//        this.joinDate = LocalDateTime.now();
//    }
    public void createProfile(String profileImage, String nickname){
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.joinDate = LocalDateTime.now();
    }
    @OneToMany(mappedBy = "user")
    private List<Evaluation> evaluations = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserStudy> userStudies = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<JoinRequest> joinRequests = new ArrayList<>();

    //기존 error 때문에 넣어둔 field => 추후 삭제 요망 (현재는 테스트 코드 용으로 사용중)
    private String validate;
    private String role;
    private String password;
}