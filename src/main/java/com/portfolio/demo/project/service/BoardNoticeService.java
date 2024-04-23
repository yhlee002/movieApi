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

    private final int BOARD_COUNT_PER_PAGE = 10; // 한페이지 당 보여줄 게시글의 수
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
     *
     * @param boardId
     * @return 단건 공지 게시글
     */
    public BoardNotice findById(Long boardId) {
        BoardNotice board = boardNoticeRepository.findBoardNoticeById(boardId);
        return board;
    }

    /**
     * 공지사항 게시글 단건 조회 + 이전글, 다음글
     *
     * @param boardId
     */
    @Transactional
    public HashMap<String, BoardNotice> selectBoardsByBoardId(Long boardId) {
        BoardNotice board = null;
        Optional<BoardNotice> boardOpt = boardNoticeRepository.findById(boardId);
        if (boardOpt.isPresent()) {
            board = boardOpt.get();
        }

        BoardNotice prevBoard = boardNoticeRepository.findPrevBoardNoticeById(boardId);
        BoardNotice nextBoard = boardNoticeRepository.findNextBoardNoticeById(boardId);
        HashMap<String, BoardNotice> boardNoticeMap = new HashMap<>();
        boardNoticeMap.put("board", board);
        boardNoticeMap.put("prevBoard", prevBoard);
        boardNoticeMap.put("nextBoard", nextBoard);

        return boardNoticeMap;
    }

    /**
     * 최근 공지사항 게시글 top {size}
     */
    public List<BoardNotice> getRecNoticeBoard(int size) {
        return boardNoticeRepository.findRecentBoardNoticesOrderByRegDate(size);
    }

    /**
     * 공지사항 게시글 수정
     *
     * @param notice
     */
    @Transactional
    public BoardNotice updateBoard(BoardNotice notice) {
        // 작성자 정보 검증
        Member member = notice.getWriter();
        if (member != null) {
            Optional<Member> opt = memberRepository.findById(member.getMemNo());

            opt.ifPresentOrElse(m -> {
                        log.info("작성자 정보(memNo : {}) : valid", m.getMemNo());

                        boardNoticeRepository.save(
                                BoardNotice.builder()
                                        .id(notice.getId())
                                        .title(notice.getTitle())
                                        .writer(member)
                                        .content(notice.getContent())
                                        .build()
                        );
                    },
                    () -> {
                        throw new IllegalStateException("존재하지 않는 회원 정보입니다.");
                    }
            );
        } else {
            throw new IllegalStateException("작성자 정보가 누락되었습니다.");
        }

        return notice;
    }

    /**
     * 공지사항 게시글 삭제
     *
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
     *
     * @param boards
     */
    public void deleteBoards(List<BoardNotice> boards) { // 자신이 작성한 글 목록에서 선택해서 삭제 가능
        boardNoticeRepository.deleteAll(boards);
    }

    /**
     * 공지사항 게시글 조회수 증가
     *
     * @param boardId
     */

    @Transactional
    public void upViewCnt(Long boardId) {
        BoardNotice notice = boardNoticeRepository.findById(boardId).get();
        notice.setViews(notice.getViews() + 1);
        boardNoticeRepository.save(notice);
    }

    /**
     * 공지사항 게시글 조회(10page씩)
     *
     * @param page
     */
    @Transactional
    public NoticePagenationVO getBoardNotices(int page) {
        Pageable pageable = PageRequest.of(page, BOARD_COUNT_PER_PAGE, Sort.by(Sort.Direction.DESC, "regDate"));
        Page<BoardNotice> pages = boardNoticeRepository.findAll(pageable);

        return NoticePagenationVO.builder()
                .totalPageCnt(pages.getTotalPages())
                .boardNoticeList(pages.getContent())
                .build();
    }

    /**
     * 공지사항 게시글 조회(검색어가 존재)
     *
     * @param page
     * @param keyword
     */
    @Transactional
    public NoticePagenationVO getBoardNoticesByTitleOrContent(String keyword, int page) {
        Pageable pageable = PageRequest.of(page, BOARD_COUNT_PER_PAGE, Sort.by("regDate").descending());
        Page<BoardNotice> pages = boardNoticeRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);

        return NoticePagenationVO.builder()
                .totalPageCnt(pages.getTotalPages())
                .boardNoticeList(pages.getContent())
                .build();
    }
}
