package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.repository.BoardNoticeRepository;
import com.portfolio.demo.project.vo.NoticePagenationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class BoardNoticeService {

    @Autowired
    BoardNoticeRepository boardNoticeRepository;

    /* 조회 */
    public List<BoardNotice> selectAllBoards() {
        return boardNoticeRepository.findAllBoardNotice();
    }

    // 게시글 단건 조회
    public BoardNotice selectBoardByBoardId(Long boardId) {
        return boardNoticeRepository.findBoardNoticeByBoarId(boardId);
    }

    // 게시글 단건 조회 + 이전글, 다음글
    public HashMap<String, BoardNotice> selectBoardsByBoardId(Long boardId) {
        BoardNotice board = boardNoticeRepository.findBoardNoticeByBoarId(boardId);
        BoardNotice prevBoard = boardNoticeRepository.findPrevBoardNoticeByBoardId(boardId);
        BoardNotice nextBoard = boardNoticeRepository.findNextBoardNoticeByBoardId(boardId);
        HashMap<String, BoardNotice> boardNoticeMap = new HashMap<>();
        boardNoticeMap.put("board", board);
        boardNoticeMap.put("prevBoard", prevBoard);
        boardNoticeMap.put("nextBoard", nextBoard);

        return boardNoticeMap;
    }

    // 최근 공지사항 게시글 top 5
    public List<BoardNotice> getRecNoticeBoard() {
        return boardNoticeRepository.findTop5ByOrderByRegDateDesc();
    }

    /* 추가(작성) */
    public BoardNotice saveBoard(String title, Long memNo, String content) {
        BoardNotice notice = new BoardNotice(null, title, memNo, content, LocalDateTime.now());
        return boardNoticeRepository.save(notice);
    }

    /* 수정 */
    public Long updateBoard(Long boardId, String title, Long memNo, String content) {
        BoardNotice newBoard = null;
        BoardNotice originBoard = boardNoticeRepository.findBoardNoticeByBoarId(boardId);

        newBoard = new BoardNotice(boardId, title, memNo, content, originBoard.getRegDate());
        boardNoticeRepository.save(newBoard);

        return newBoard.getBoardId();
    }

    /* 삭제 */
    public void deleteBoardByBoardId(Long boardId) {
        BoardNotice board = boardNoticeRepository.findBoardNoticeByBoarId(boardId);
        boardNoticeRepository.delete(board);
    }

    public void deleteBoards(List<BoardNotice> boards) { // 자신이 작성한 글 목록에서 선택해서 삭제 가능

        boardNoticeRepository.deleteAll(boards);
    }

    /* 페이지 네이션 */
    private static final int BOARD_COUNT_PER_PAGE = 10; // 한페이지 당 보여줄 게시글의 수

    // 기본 화면에서의 페이지네이션 리스트 뷰
    public NoticePagenationVO getNoticeListView(int pageNum) {
        int totalBoardCnt = boardNoticeRepository.findBoardNoticeTotalCount();
        int startRow = 0;
        List<BoardNotice> boardNoticeList = null;
        NoticePagenationVO noticePagenationVO = null;
        if (totalBoardCnt > 0) {
            startRow = (pageNum - 1) * BOARD_COUNT_PER_PAGE;

            boardNoticeList = boardNoticeRepository.findBoardNoticeListView(startRow, BOARD_COUNT_PER_PAGE);
        } else {
            pageNum = 0;
        }

        int endRow = startRow * BOARD_COUNT_PER_PAGE;

        noticePagenationVO = new NoticePagenationVO(totalBoardCnt, pageNum, boardNoticeList, BOARD_COUNT_PER_PAGE, startRow, endRow);

        return noticePagenationVO;
    }

    // 검색어가 존재할 때 페이지네이션 리스트 뷰
    public NoticePagenationVO getNoticeListViewByTitleOrContent(int pageNum, String titleOrContent) {
        int totalBoardCnt = boardNoticeRepository.findBoardNoticeSearchResultTotalCountTC(titleOrContent);
        int startRow = 0;
        List<BoardNotice> boardNoticeList = null;
        NoticePagenationVO noticePagenationVO = null;
        if (totalBoardCnt > 0) {
            startRow = (pageNum - 1) * BOARD_COUNT_PER_PAGE;

            boardNoticeList = boardNoticeRepository.findBoardNoticeListViewByTitleOrContent(titleOrContent, startRow, BOARD_COUNT_PER_PAGE);
        } else {
            pageNum = 0;
        }

        int endRow = startRow * BOARD_COUNT_PER_PAGE;

        noticePagenationVO = new NoticePagenationVO(totalBoardCnt, pageNum, boardNoticeList, BOARD_COUNT_PER_PAGE, startRow, endRow);

        return noticePagenationVO;
    }
}
