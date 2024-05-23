package com.example.swip.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRelationship {
    private Boolean is_owner;
    private Boolean is_member;
    private Boolean is_favorite;


    @QueryProjection
    public UserRelationship(Boolean is_owner, Boolean is_member, Boolean is_favorite) {
        this.is_owner = is_owner;
        this.is_member = is_member;
        this.is_favorite = is_favorite;
    }
}
