package com.example.swip.api;

import com.example.swip.dto.BoardDetailResponse;
import com.example.swip.dto.BoardResponse;
import com.example.swip.dto.BoardSaveRequest;
import com.example.swip.dto.BoardUpdateRequest;
import com.example.swip.entity.Board;
import com.example.swip.service.BoardService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class BoardApiController {

    private final BoardService boardService;

    //저장
    @PostMapping("/board")
    public Long saveBoard(@RequestBody BoardSaveRequest dto){

        Long savedBoard = boardService.saveBoard(dto);
        System.out.println("savedBoard = " + savedBoard);
        return savedBoard;
    }

    //조회
    @GetMapping("/board")
    public Result showBoard(){
        List<Board> allBoards = boardService.findAllBoards();

        //Dto 로 변환
        List<BoardResponse> result = allBoards.stream()
                .map(board -> new BoardResponse(board.getId(), board.getTitle(), board.getWriter().getId()))
                .collect(Collectors.toList());

        return new Result(result); // TODO: Result 타입으로 한번 감싸기
    }

    @GetMapping("/board/{id}")
    public BoardDetailResponse showBoardDetail(@PathVariable("id") Long boardId){
        Board findBoard = boardService.findBoard(boardId);

        // Dto 로 변환
        BoardDetailResponse response = new BoardDetailResponse(
                findBoard.getId(),
                findBoard.getTitle(),
                findBoard.getContent(),
                findBoard.getWriter().getId(),
                findBoard.getCreated_time(),
                findBoard.getUpdated_time());

        return response;
    }

    //수정
    @PutMapping("/board/{id}/edit")
    public Long updateBoardDetail(
            @PathVariable("id") Long boardId,
            @RequestBody BoardUpdateRequest boardUpdateRequest
    ){
        Long updatedBoardId = boardService.updateBoard(boardId, boardUpdateRequest);
        return updatedBoardId;
    }

    //삭제
    @DeleteMapping("/board/{id}")
    public Long deleteBoardDetail(@PathVariable("id") Long boardId){
        Long deletedBoardId = boardService.deleteBoard(boardId);
        return deletedBoardId;
    }

    // List 값을 Result로 한 번 감싸서 return하기 위한 class
    @Data
    @AllArgsConstructor
    public static class Result<T>{
        private T data;
    }

}
