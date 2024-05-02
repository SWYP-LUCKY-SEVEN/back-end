package com.example.swip.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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

    private String profile_image;

    private String email;

    private String nickname;

    private LocalDateTime join_date;

    private LocalDateTime withdrawal_date;

    @OneToMany(mappedBy = "user")
    private List<Evaluation> evaluations = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserStudy> userStudies = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<JoinRequest> joinRequests = new ArrayList<>();

    //기존 error 때문에 넣어둔 field => 추후 삭제 요망
    private String validate;
    private String role;
    private String profileName;
    private String password;
}