package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.BoardNoticeRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.vo.BoardNoticeVO;
import com.portfolio.demo.project.vo.NoticePagenationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
     * 전체 공지사항 게시글 조회
     *
     * @param page
     * @param size
     */
    @Transactional
    public NoticePagenationVO getAllBoards(int page, Integer size) {
        if (size == null) size = BOARD_COUNT_PER_PAGE;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regDate"));
        Page<BoardNotice> pages = boardNoticeRepository.findAll(pageable);

        List<BoardNotice> list = pages.getContent();

        log.info("조회된 게시글 수 : {}", list.size());

        List<BoardNoticeVO> vos = new ArrayList<>();
        list.forEach(boardNotice -> {
            Member writer = (Member) Hibernate.unproxy(boardNotice.getWriter());
            boardNotice.updateWriter(writer);
            vos.add(BoardNoticeVO.create(boardNotice));
        });

        return NoticePagenationVO.builder()
                .boardNoticeList(vos)
                .totalPageCnt(pages.getTotalPages())
                .build();
    }

    /**
     * 공지사항 게시글 식별번호로 단건 조회
     *
     * @param boardId
     * @return 단건 공지 게시글
     */
    public BoardNotice findById(Long boardId) {
        return boardNoticeRepository.findBoardNoticeById(boardId);
    }

    /**
     * 공지사항 게시글 식별번호로 이전글 조회
     *
     * @param id
     * @return
     */
    public BoardNotice findPrevById(Long id) {
        return boardNoticeRepository.findPrevBoardNoticeById(id);
    }

    /**
     * 공지사항 게시글 식별번호로 다음글 조회
     *
     * @param id
     * @return
     */
    public BoardNotice findNextById(Long id) {
        return boardNoticeRepository.findNextBoardNoticeById(id);
    }

    /**
     * 최근 공지사항 게시글 top {size}
     */
    public List<BoardNotice> getRecentNoticeBoard(int size) {
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

            opt.ifPresentOrElse(
                    m -> {
                        log.info("작성자 정보(memNo : {}) : valid", m.getMemNo());

                        boardNoticeRepository.save(notice);
                    },
                    () -> {
                        throw new IllegalStateException("존재하지 않는 회원입니다.");
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
    public void upViewCntById(Long boardId) {
        BoardNotice notice = boardNoticeRepository.findById(boardId).get();
        notice.updateViewCount();
        boardNoticeRepository.save(notice);
    }

    /**
     * 공지사항 게시글 조회(검색어가 존재)
     *
     * @param page
     * @param keyword
     */
    @Transactional
    public NoticePagenationVO getBoardNoticePagenationByTitleOrContent(int page, Integer size, String keyword) {
        if (size == null) size = BOARD_COUNT_PER_PAGE;
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<BoardNotice> pages = boardNoticeRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        List<BoardNotice> list = pages.getContent();

        List<BoardNoticeVO> vos = new ArrayList<>();
        list.forEach(boardNotice -> {
            Member writer = (Member) Hibernate.unproxy(boardNotice.getWriter());
            boardNotice.updateWriter(writer);
            vos.add(BoardNoticeVO.create(boardNotice));
        });

        return NoticePagenationVO.builder()
                .totalPageCnt(pages.getTotalPages())
                .boardNoticeList(vos)
                .build();
    }
}
