package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.Board;
import com.portfolio.demo.project.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BoardService {

    @Autowired
    BoardRepository boardRepository;

    /* 조회 */
    public List<Board> selectAllBoards() {
        return boardRepository.findAll();
    }

    public Board selectBoardByBoardId(Long boardId) {
//        return boardRepository.findByBoardId(boardId);
        Board b = null;
        Optional<Board> board = boardRepository.findById(boardId);
        if(board.isPresent()) {
            b = board.get();
        }
        return b;
    }

    public List<Board> selectBoardsByWriterNo(Long writerNo) {
        return boardRepository.findAllByWriterNo(writerNo);
    }

//    public List<Board> selectBoardsByTitle(String title) {
//        return boardRepository.findAllByTitle(title);
//    }
//
//    public List<Board> selectBoardsByContent(String content) {
//        return boardRepository.findAllByContent(content);
//    }

    public List<Board> selectBoardsByTitleAndContent(String titleOrContent) {
        return boardRepository.findAllByTitleAndContent(titleOrContent);
    }

    public List<Board> selectBoardsByWriterName(String writerName) {
        return boardRepository.findAllByWriterName(writerName);
    }

    /* 수정 */
    public Long updateBoard(Board board) { // 해당 board에 boardId, memNo, regDt 등이 담겨 있다면 다른 내용들도 따로 set하지 않고 바로 save해도 boardId, memNo등이 같으니 변경을 감지하지 않을까?

        Optional<Board> originBoard = boardRepository.findById(board.getBoardId());
        if (originBoard.isPresent()) {
            boardRepository.save(originBoard.get());
        }

        return board.getBoardId();
    }

    /* 삭제 */
    public void deleteBoard(Long boardId) {
        boardRepository.deleteById(boardId);
    }
    
    public void deleteBoards(List<Board> boards) { // 자신이 작성한 글 목록에서 선택해서 삭제 가능
        boardRepository.deleteAll(boards);
    }
}
