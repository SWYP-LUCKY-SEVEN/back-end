package com.example.swip.service;

import com.example.swip.dto.BoardSaveRequest;
import com.example.swip.dto.BoardUpdateRequest;
import com.example.swip.entity.Board;
import com.example.swip.entity.User;
import com.example.swip.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserService userService;


    //저장
    @Transactional
    public Long saveBoard(BoardSaveRequest boardSaveRequest){
        User writer = userService.findUserById(boardSaveRequest.getWriterId()); //작성자 정보 조회
        Board savedBoard = boardRepository.save(boardSaveRequest.toEntity(writer));
        return savedBoard.getId();
    }

    //조회
    public Board findBoard(Long id){
        Board findBoard = boardRepository.findById(id).orElse(null);
        return findBoard;
    }

    public List<Board> findAllBoards(){
        List<Board> allBoards = boardRepository.findAll();
        return allBoards;
    }

    //수정
    @Transactional
    public Long updateBoard(Long id, BoardUpdateRequest boardUpdateRequest) {
        Board findBoard = boardRepository.findById(id).orElse(null);

        // JPA의 영속성 컨텍스트에 의해 entity 객체의 값만 변경하면 자동으로 변경 사항 반영하여 update 진행.
        // reoisitory.update 필요 없음.
        if(findBoard != null){
            findBoard.updateBoard(boardUpdateRequest.getTitle(), boardUpdateRequest.getContent());
        }
        // TODO: null 예외 처리

        return id;
    }
    //삭제
    @Transactional
    public Long deleteBoard(Long id){
        Board findBoard = boardRepository.findById(id).orElse(null);
        boardRepository.delete(findBoard);

        return id;
    }
}
