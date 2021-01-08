package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.vo.ImpressionPagenationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BoardImpService {

    @Autowired
    BoardImpRepository boardImpRepository;

    /* 조회 */
    public List<BoardImp> selectAllBoards() {
        return boardImpRepository.findAllBoardImp();
    }

    public HashMap<String, BoardImp> selectBoardByBoardId(Long boardId) {
        BoardImp board = boardImpRepository.findBoardImpByBoarId(boardId);
        BoardImp prevBoard = boardImpRepository.findPrevBoardImpByBoardId(boardId);
        BoardImp nextBoard = boardImpRepository.findNextBoardImpByBoardId(boardId);
        HashMap<String, BoardImp> boardNoticeMap = new HashMap<>();
        boardNoticeMap.put("board", board);
        boardNoticeMap.put("prevBoard", prevBoard);
        boardNoticeMap.put("nextBoard", nextBoard);

        return boardNoticeMap;
    }

    public List<BoardImp> selectBoardsByWriterNo(Long writerNo) {
        return boardImpRepository.findAllBoardImpByWriterNo(writerNo);
    }

    public List<BoardImp> selectBoardsByTitleAndContent(String titleOrContent) {
        return boardImpRepository.findAllBoardImpByTitleAndContent(titleOrContent);
    }


    /* 수정 */
    public Long updateBoard(BoardImp board) { // 해당 board에 boardId, memNo, regDt 등이 담겨 있다면 다른 내용들도 따로 set하지 않고 바로 save해도 boardId, memNo등이 같으니 변경을 감지하지 않을까?

        Optional<BoardImp> originBoard = boardImpRepository.findById(board.getBoardId());
//        if (originBoard.isPresent()) {
//            boardNoticeRepository.save(originBoard.get());
//        }
        originBoard.ifPresent(boardNotice -> boardImpRepository.save(boardNotice));

        return board.getBoardId();
    }

    /* 삭제 */
    public void deleteBoard(Long boardId) {
        boardImpRepository.deleteById(boardId);
    }

    public void deleteBoards(List<BoardImp> boards) { // 자신이 작성한 글 목록에서 선택해서 삭제 가능
        boardImpRepository.deleteAll(boards);
    }

    /* 페이지 네이션 */
    private static final int BOARD_COUNT_PER_PAGE = 10; // 한페이지 당 보여줄 게시글의 수

    public ImpressionPagenationVO getImpListView(int pageNum) {
        int totalBoardCnt = boardImpRepository.findBoardImpTotalCount();
        int startRow = 0;
        List<BoardImp> boardImpList = null;
        ImpressionPagenationVO impPagenationVO = null;
        if (totalBoardCnt > 0) {
            startRow = (pageNum - 1) * BOARD_COUNT_PER_PAGE;

            boardImpList = boardImpRepository.findBoardImpListView(startRow, BOARD_COUNT_PER_PAGE);
        } else {
            pageNum = 0;
        }

        int endRow = startRow * BOARD_COUNT_PER_PAGE;

        impPagenationVO = new ImpressionPagenationVO(totalBoardCnt, pageNum, boardImpList, BOARD_COUNT_PER_PAGE, startRow, endRow);

        return impPagenationVO;
    }
}
