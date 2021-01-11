package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.vo.ImpressionPagenationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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

    // 게시글 단건 조회
    public BoardImp selectBoardByBoardId(Long boardId) {
        return boardImpRepository.findBoardImpByBoarId(boardId);
    }

    // 게시글 단건 조회 + 이전글, 다음글
    public HashMap<String, BoardImp> selectBoardsByBoardId(Long boardId) {
        BoardImp board = boardImpRepository.findBoardImpByBoarId(boardId);
        BoardImp prevBoard = boardImpRepository.findPrevBoardImpByBoardId(boardId);
        BoardImp nextBoard = boardImpRepository.findNextBoardImpByBoardId(boardId);
        HashMap<String, BoardImp> boardNoticeMap = new HashMap<>();
        boardNoticeMap.put("board", board);
        boardNoticeMap.put("prevBoard", prevBoard);
        boardNoticeMap.put("nextBoard", nextBoard);

        return boardNoticeMap;
    }

    // 인기 게시글 top 5
    public List<BoardImp> getFavImpBoard() {
        return boardImpRepository.findTop5ByOrderByViewsDesc();
    }

    /* 추가(작성) */
    public BoardImp saveBoard(String title, Long memNo, String content) {
        BoardImp imp = new BoardImp(null, title, content, memNo, LocalDateTime.now());
        return boardImpRepository.save(imp);
    }

    /* 수정 */
    @Transactional
    public Long updateBoard(Long boardId, String title, Long memNo, String content) { // 해당 board에 boardId, memNo, regDt 등이 담겨 있다면 다른 내용들도 따로 set하지 않고 바로 save해도 boardId, memNo등이 같으니 변경을 감지하지 않을까?

        BoardImp newBoard = null;
        BoardImp originBoard = boardImpRepository.findBoardImpByBoarId(boardId);
        if (originBoard != null) {
            newBoard = new BoardImp(boardId, title, content, memNo, originBoard.getRegDate());
            boardImpRepository.save(newBoard);
        }

        return boardId;
    }

    /* 삭제 */
    @Transactional
    public void deleteBoardByBoardId(Long boardId) {
        BoardImp board = boardImpRepository.findBoardImpByBoarId(boardId);
        boardImpRepository.delete(board);
    }

    public void deleteBoards(List<BoardImp> boards) { // 자신이 작성한 글 목록에서 선택해서 삭제 가능
        boardImpRepository.deleteAll(boards);
    }

    /* 페이지 네이션 */
    private static final int BOARD_COUNT_PER_PAGE = 10; // 한페이지 당 보여줄 게시글의 수

    @Transactional
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

    /* 검색 기능 (작성자명) */
    @Transactional
    public ImpressionPagenationVO getImpListViewByWriterName(int pageNum, String writer) {
        int totalBoardCnt = boardImpRepository.findBoardNoticeSearchResultTotalCountWN(writer);
        int startRow = 0;
        List<BoardImp> boardImpList = null;
        ImpressionPagenationVO impressionPagenationVO = null;
        if (totalBoardCnt > 0) {
            startRow = (pageNum - 1) * BOARD_COUNT_PER_PAGE;
            boardImpList = boardImpRepository.findBoardImpListViewByWriterName(writer, startRow, BOARD_COUNT_PER_PAGE);
        } else {
            pageNum = 0;
        }

        int endRow = startRow * BOARD_COUNT_PER_PAGE;
        impressionPagenationVO = new ImpressionPagenationVO(totalBoardCnt, pageNum, boardImpList, BOARD_COUNT_PER_PAGE, startRow, endRow);

        return impressionPagenationVO;
    }

    /* 검색 기능 (제목 또는 내용) */
    @Transactional
    public ImpressionPagenationVO getImpListViewByTitleAndContent(int pageNum, String titleOrContent) {
        int totalBoardCnt = boardImpRepository.findBoardImpSearchResultTotalCountTC(titleOrContent);
        int startRow = 0;
        List<BoardImp> boardImpList = null;
        ImpressionPagenationVO impressionPagenationVO = null;
        if (totalBoardCnt > 0) {
            startRow = (pageNum - 1) * BOARD_COUNT_PER_PAGE;
            boardImpList = boardImpRepository.findBoardImpListViewByTitleOrContent(titleOrContent, startRow, BOARD_COUNT_PER_PAGE);
        } else {
            pageNum = 0;
        }

        int endRow = startRow * BOARD_COUNT_PER_PAGE;
        impressionPagenationVO = new ImpressionPagenationVO(totalBoardCnt, pageNum, boardImpList, BOARD_COUNT_PER_PAGE, startRow, endRow);

        return impressionPagenationVO;
    }
}
