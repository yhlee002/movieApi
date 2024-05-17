package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.BoardNoticeRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.vo.BoardNoticeVO;
import com.portfolio.demo.project.vo.NoticePagenationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardNoticeService {

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
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regDate"));
        Page<BoardNotice> pages = boardNoticeRepository.findAll(pageable);

        List<BoardNotice> list = pages.getContent();

        log.info("조회된 게시글 수 : {}", list.size());

        List<BoardNoticeVO> vos = list.stream().map(BoardNoticeVO::create).toList();

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
    public BoardNoticeVO findById(Long boardId) {
        BoardNotice board = boardNoticeRepository.findById(boardId).orElse(null);

        BoardNoticeVO vo = null;

        if (board != null) {
            vo = BoardNoticeVO.create(board);
        } else {
            throw new IllegalStateException("해당 아이디의 게시글 정보가 존재하지 않습니다.");
        }

        return vo;
    }

    /**
     * 공지사항 게시글 식별번호로 이전글 조회
     *
     * @param id
     * @return
     */
    public BoardNoticeVO findPrevById(Long id) {
        BoardNotice board = boardNoticeRepository.findPrevBoardNoticeById(id);

        BoardNoticeVO vo = null;

        if (board != null) {
            vo = BoardNoticeVO.create(board);
        }

        return vo;
    }

    /**
     * 공지사항 게시글 식별번호로 다음글 조회
     *
     * @param id
     * @return
     */
    public BoardNoticeVO findNextById(Long id) {
        BoardNotice board = boardNoticeRepository.findNextBoardNoticeById(id);

        BoardNoticeVO vo = null;

        if (board != null) {
            vo = BoardNoticeVO.create(board);
        }

        return vo;
    }

    /**
     * 최근 공지사항 게시글 top {size}
     */
    public List<BoardNoticeVO> getRecentNoticeBoard(int size) {
        List<BoardNotice> result = boardNoticeRepository.findRecentBoardNoticesOrderByRegDate(size);
        return result.stream().map(BoardNoticeVO::create).toList();
    }

    /**
     * 공지사항 게시글 수정
     *
     * @param notice
     */
    @Transactional
    public BoardNoticeVO updateBoard(BoardNoticeVO notice) {
        Member member = memberRepository.findById(notice.getWriterId()).orElse(null);

        BoardNoticeVO vo = null;

        if (member != null) {
            BoardNotice modified = boardNoticeRepository.save(
                    BoardNotice.builder()
                            .id(notice.getId())
                            .title(notice.getTitle())
                            .content(notice.getContent())
                            .writer(member)
                            .views(notice.getViews())
                            .build()
            );

            vo = BoardNoticeVO.create(modified);
        } else {
            throw new IllegalStateException("해당 아이디의 회원 정보가 존재하지 않습니다.");
        }

        return vo;
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
    public void deleteBoards(List<BoardNoticeVO> boards) { // 자신이 작성한 글 목록에서 선택해서 삭제 가능
        List<BoardNotice> list = boards.stream().map(b -> BoardNotice.builder()
                .id(b.getId())
                .title(b.getTitle())
                .content(b.getContent())
                .writer(memberRepository.findById(b.getWriterId()).orElse(null))
                .views(b.getViews())
                .build()
        ).toList();

        boardNoticeRepository.deleteAll(list);
    }

    /**
     * 공지사항 게시글 조회수 증가
     *
     * @param boardId
     */

    @Transactional
    public void upViewCntById(Long boardId) {
        BoardNotice notice = boardNoticeRepository.findById(boardId).orElse(null);

        if (notice != null) {
            notice.updateViewCnt();
            boardNoticeRepository.save(notice);
        } else {
            throw new IllegalStateException("해당 아이디의 게시글 정보가 존재하지 않습니다.");
        }
    }

    /**
     * 공지사항 게시글 조회(검색어가 존재)
     *
     * @param page
     * @param keyword
     */
    @Transactional
    public NoticePagenationVO getBoardNoticePagenationByTitleOrContent(int page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<BoardNotice> pages = boardNoticeRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        List<BoardNotice> list = pages.getContent();

        List<BoardNoticeVO> vos = list.stream().map(BoardNoticeVO::create).toList();

        return NoticePagenationVO.builder()
                .totalPageCnt(pages.getTotalPages())
                .boardNoticeList(vos)
                .build();
    }
}
