package com.example.swip.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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
    private String profileName;

    //private String profileInterests;

    //private String extraInfo;
}