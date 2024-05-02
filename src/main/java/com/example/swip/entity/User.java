package com.example.swip.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

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

    @JsonIgnore
    private String password;

    private String role;

    private String validate;

    //private Long profile;
    private String nickname;

    private String profileImage;

    private Date joinDate;
    private Date withdrawalDate;

    //private String extraInfo;
    public void updateProfile(String profileImage, String nickname){
        this.profileImage = profileImage;
        this.nickname = nickname;
    }
}