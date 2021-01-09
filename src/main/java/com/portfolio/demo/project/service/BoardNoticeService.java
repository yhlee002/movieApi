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
import java.util.Optional;

@Slf4j
@Service
public class BoardNoticeService {

    @Autowired
    BoardNoticeRepository boardNoticeRepository;

    /* 조회 */
    public List<BoardNotice> selectAllBoards() {
        return boardNoticeRepository.findAllBoardNotice();
    }

    public HashMap<String, BoardNotice> selectBoardByBoardId(Long boardId) {
        BoardNotice board = boardNoticeRepository.findBoardNoticeByBoarId(boardId);
        BoardNotice prevBoard = boardNoticeRepository.findPrevBoardNoticeByBoardId(boardId);
        BoardNotice nextBoard = boardNoticeRepository.findNextBoardNoticeByBoardId(boardId);
        HashMap<String, BoardNotice> boardNoticeMap = new HashMap<>();
        boardNoticeMap.put("board", board);
        boardNoticeMap.put("prevBoard", prevBoard);
        boardNoticeMap.put("nextBoard", nextBoard);

        return boardNoticeMap;
    }

//    public List<BoardNotice> selectBoardsByWriterNo(Long writerNo) {
//        return boardNoticeRepository.findAllBoardNoticeByWriterNo(writerNo);
//    }

    public List<BoardNotice> selectBoardsByTitleAndContent(String titleOrContent) {
        return boardNoticeRepository.findAllBoardNoticeByTitleAndContent(titleOrContent);
    }

    /* 추가(작성) */
    public BoardNotice saveBoard(String title, Long memNo, String content) {
        BoardNotice notice = new BoardNotice(null, title, memNo, content, LocalDateTime.now());
        return boardNoticeRepository.save(notice);
    }

    /* 수정 */
    public Long updateBoard(BoardNotice board) { // 해당 board에 boardId, memNo, regDt 등이 담겨 있다면 다른 내용들도 따로 set하지 않고 바로 save해도 boardId, memNo등이 같으니 변경을 감지하지 않을까?

        Optional<BoardNotice> originBoard = boardNoticeRepository.findById(board.getBoardId());
//        if (originBoard.isPresent()) {
//            boardNoticeRepository.save(originBoard.get());
//        }
        originBoard.ifPresent(boardNotice -> boardNoticeRepository.save(boardNotice));

        return board.getBoardId();
    }

    /* 삭제 */
    public void deleteBoard(Long boardId) {
        boardNoticeRepository.deleteById(boardId);
    }

    public void deleteBoards(List<BoardNotice> boards) { // 자신이 작성한 글 목록에서 선택해서 삭제 가능
        boardNoticeRepository.deleteAll(boards);
    }

    /* 페이지 네이션 */
    private static final int BOARD_COUNT_PER_PAGE = 10; // 한페이지 당 보여줄 게시글의 수

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
}
