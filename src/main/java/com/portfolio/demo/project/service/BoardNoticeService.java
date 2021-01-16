package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.BoardNoticeRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.vo.NoticePagenationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BoardNoticeService {

    @Autowired
    BoardNoticeRepository boardNoticeRepository;

    @Autowired
    MemberRepository memberRepository;

    /* 조회 */
    public List<BoardNotice> selectAllBoards() {
        return boardNoticeRepository.findAllBoardNotice();
    }

    // 게시글 단건 조회
    public BoardNotice selectBoardByBoardId(Long boardId) {
        BoardNotice board = null;
        Optional<BoardNotice> boardOpt = boardNoticeRepository.findById(boardId);
        if (boardOpt.isPresent()) {
            board = boardOpt.get();
        }
        return board;
    }

    // 게시글 단건 조회 + 이전글, 다음글
    @Transactional
    public HashMap<String, BoardNotice> selectBoardsByBoardId(Long boardId) {
        BoardNotice board = null;
        Optional<BoardNotice> boardOpt = boardNoticeRepository.findById(boardId);
        if (boardOpt.isPresent()) {
            board = boardOpt.get();
        }

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
    @Transactional
    public BoardNotice saveBoard(String title, Long memNo, String content) {
        Member member = null;
        Optional<Member> memOpt = memberRepository.findById(memNo);
        if (memOpt.isPresent()) {
            member = memOpt.get();
        }

        return boardNoticeRepository.save(
                BoardNotice.builder()
                        .id(null)
                        .title(title)
                        .writer(member)
                        .content(content)
                        .build()
        );
    }

    /* 수정 */
    @Transactional
    public Long updateBoard(Long boardId, String title, Long memNo, String content) {
        BoardNotice newBoard = null;
        BoardNotice originBoard = null;

        Optional<BoardNotice> originBoardOpt = boardNoticeRepository.findById(boardId);
        if (originBoardOpt.isPresent()) {
            originBoard = originBoardOpt.get();
        }

        Member member = null;
        Optional<Member> memOpt = memberRepository.findById(memNo);
        if (memOpt.isPresent()) {
            member = memOpt.get();
        }

        return boardNoticeRepository.save(BoardNotice.builder()
                .id(boardId)
                .title(title)
                .writer(member)
                .content(content)
                .build()).getId();
    }

    /* 삭제 */
    @Transactional
    public void deleteBoardByBoardId(Long boardId) {
        Optional<BoardNotice> boardOpt = boardNoticeRepository.findById(boardId);
        if (boardOpt.isPresent()) {
            boardNoticeRepository.delete(boardOpt.get());
        }
    }

    public void deleteBoards(List<BoardNotice> boards) { // 자신이 작성한 글 목록에서 선택해서 삭제 가능

        boardNoticeRepository.deleteAll(boards);
    }

    // 게시글 조회수 증가
    @Transactional
    public void upViewCnt(Long boardId) {
        BoardNotice notice = boardNoticeRepository.findById(boardId).get();
        notice.setViews(notice.getViews()+1);
        boardNoticeRepository.save(notice);
    }

    /* 페이지 네이션 */
    private static final int BOARD_COUNT_PER_PAGE = 10; // 한페이지 당 보여줄 게시글의 수

    // 기본 화면에서의 페이지네이션 리스트 뷰
    @Transactional
    public NoticePagenationVO getNoticeListView(int pageNum) {
        int totalBoardCnt = boardNoticeRepository.findCount();
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
    @Transactional
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
