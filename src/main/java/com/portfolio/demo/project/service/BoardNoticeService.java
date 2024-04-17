package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.BoardNoticeRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.vo.NoticePagenationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardNoticeService {

    private static final int BOARD_COUNT_PER_PAGE = 10; // 한페이지 당 보여줄 게시글의 수
    private final BoardNoticeRepository boardNoticeRepository;
    
    private final MemberRepository memberRepository;

    /**
     * @deprecated 페이지네이션되는 api 사용으로 사용되지 않음
     * 전체 공지사항 게시글 조회
     */
    public List<BoardNotice> getAllBoards() {
        return boardNoticeRepository.findAll();
    }

    /**
     * 공지사항 게시글 단건 조회
     * @param boardId
     * @return 단건 공지 게시글
     */
    public BoardNotice findById(Long boardId) {
        BoardNotice board = boardNoticeRepository.findBoardNoticeByBoardId(boardId);
        return board;
    }

    /**
     * 공지사항 게시글 단건 조회 + 이전글, 다음글
     * @param boardId
     */
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

    /**
     * 최근 공지사항 게시글 top 5
     */
    public List<BoardNotice> getRecNoticeBoard() {
        return boardNoticeRepository.findTop5ByOrderByRegDateDesc();
    }

    /**
     * 공지사항 게시글 수정
     * @param notice
     */
    @Transactional
    public Long updateBoard(BoardNotice notice) {
        Member member = notice.getWriter();

        return boardNoticeRepository.save(
                BoardNotice.builder()
                        .id(notice.getId())
                        .title(notice.getTitle())
                        .writer(member)
                        .content(notice.getContent())
                        .build()
        ).getId();
    }

    /**
     * 공지사항 게시글 삭제
     * @param boardId
     */
    @Transactional
    public void deleteBoardByBoardId(Long boardId) {
        Optional<BoardNotice> boardOpt = boardNoticeRepository.findById(boardId);
        if (boardOpt.isPresent()) {
            boardNoticeRepository.delete(boardOpt.get());
        }
    }

    /**
     * 선택된 공지사항 게시글 삭제
     * @param boards
     */
    public void deleteBoards(List<BoardNotice> boards) { // 자신이 작성한 글 목록에서 선택해서 삭제 가능
        boardNoticeRepository.deleteAll(boards);
    }

    /**
     * 공지사항 게시글 조회수 증가
     * @param  boardId
     */

    @Transactional
    public void upViewCnt(Long boardId) {
        BoardNotice notice = boardNoticeRepository.findById(boardId).get();
        notice.setViews(notice.getViews()+1);
        boardNoticeRepository.save(notice);
    }

    /**
     * 공지사항 게시글 조회(10page씩)
     * @param page
     */
    @Transactional
    public NoticePagenationVO getBoardNotices(int page) {
        Pageable pageable = PageRequest.of(page, BOARD_COUNT_PER_PAGE, Sort.by(Sort.Direction.DESC, "regDate"));
        Page<BoardNotice> pages = boardNoticeRepository.findBoardNotices(pageable);

        return NoticePagenationVO.builder()
                .totalPageCnt(pages.getTotalPages())
                .boardNoticeList(pages.getContent())
                .build();
    }

    /**
     * 공지사항 게시글 조회(검색어가 존재)
     * @param page
     * @param keyword
     */
    @Transactional
    public NoticePagenationVO getBoardNoticesByTitleOrContent(String keyword, int page) {
        Pageable pageable = PageRequest.of(page, BOARD_COUNT_PER_PAGE, Sort.by("regDate").descending());
        Page<BoardNotice> pages = boardNoticeRepository.findByTitleOrContentContaining(keyword, pageable);

        return NoticePagenationVO.builder()
                .totalPageCnt(pages.getTotalPages())
                .boardNoticeList(pages.getContent())
                .build();
    }
}
