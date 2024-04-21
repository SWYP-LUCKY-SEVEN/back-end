package com.example.swip.dto;

import com.example.swip.entity.Board;
import com.example.swip.entity.User;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardSaveRequest {

    private String title;
    private String content;
    private Long writerId; //작성자 ID

    public Board toEntity(User writer) {
        return Board.builder()
                .title(this.title)
                .content(this.content)
                .writer(writer)
                .build();
    }
}
