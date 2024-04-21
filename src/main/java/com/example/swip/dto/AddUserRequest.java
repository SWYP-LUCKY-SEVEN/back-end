package com.example.swip.dto;

import com.example.swip.entity.Board;
import com.example.swip.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddUserRequest {
    private String email;
    private String password;

    public User toEntity() {
        return User.builder()
                .email(this.email)
                .password(this.password)
                .build();
    }
}