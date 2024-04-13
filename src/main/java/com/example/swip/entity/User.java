package com.example.swip.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private long id;
    private String email;

    @JsonIgnore
    private String password;

    private String role;

    private String extraInfo;
}