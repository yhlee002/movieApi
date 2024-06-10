package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.BoardNoticeRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.dto.board.BoardNoticeParam;
import com.portfolio.demo.project.dto.board.NoticePagenationParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public NoticePagenationParam getAllBoards(int page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regDate"));
        Page<BoardNotice> pages = boardNoticeRepository.findAll(pageable);

        List<BoardNotice> list = pages.getContent();

        log.info("조회된 게시글 수 : {}", list.size());

        List<BoardNoticeParam> vos = list.stream().map(BoardNoticeParam::create).toList();

        return NoticePagenationParam.builder()
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
    public BoardNoticeParam findById(Long boardId) {
        BoardNotice board = boardNoticeRepository.findById(boardId).orElse(null);

        BoardNoticeParam vo = null;

        if (board != null) {
            vo = BoardNoticeParam.create(board);
        } else {
            throw new IllegalStateException("해당 아이디의 게시글 정보가 존재하지 않습니다.");
//            log.error("해당 아이디의 게시글 정보가 존재하지 않습니다.");
        }

        return vo;
    }

    /**
     * 공지사항 게시글 식별번호로 이전글 조회
     *
     * @param id
     * @return
     */
    public BoardNoticeParam findPrevById(Long id) {
        BoardNotice board = boardNoticeRepository.findPrevBoardNoticeById(id);

        BoardNoticeParam vo = null;

        if (board != null) {
            vo = BoardNoticeParam.create(board);
        }

        return vo;
    }

    /**
     * 공지사항 게시글 식별번호로 다음글 조회
     *
     * @param id
     * @return
     */
    public BoardNoticeParam findNextById(Long id) {
        BoardNotice board = boardNoticeRepository.findNextBoardNoticeById(id);

        BoardNoticeParam vo = null;

        if (board != null) {
            vo = BoardNoticeParam.create(board);
        }

        return vo;
    }

    /**
     * 최근 공지사항 게시글 top {size}
     */
    public List<BoardNoticeParam> getRecentNoticeBoard(int size) {
        List<BoardNotice> result = boardNoticeRepository.findRecentBoardNoticesOrderByRegDate(size);
        return result.stream().map(BoardNoticeParam::create).toList();
    }

    /**
     * 공지사항 게시글 작성
     *
     * @param param
     */
    @Transactional
    public Long saveBoard(BoardNoticeParam param) {
        Member user = memberRepository.findById(param.getWriterId()).orElse(null);

        BoardNotice board = BoardNotice.builder()
                .title(param.getTitle())
                .content(param.getContent())
                .writer(user)
                .views(0)
                .build();

        boardNoticeRepository.save(board);

        return board.getId();
}

/**
 * 공지사항 게시글 수정
 *
 * @param param
 */
@Transactional
public Long updateBoard(BoardNoticeParam param) {
    BoardNotice board = boardNoticeRepository.findById(param.getId()).orElse(null);

    if (board != null) {
        board.updateTitle(param.getTitle());
        board.updateContent(param.getContent());
    } else {
        throw new IllegalStateException("해당 아이디의 게시글 정보가 존재하지 않습니다.");
    }

    return board.getId();
}

/**
 * 공지사항 게시글 삭제
 *
 * @param boardId
 */
@Transactional
public void deleteBoardByBoardId(Long boardId) {
    boardNoticeRepository.findById(boardId).ifPresent(boardNoticeRepository::delete);
}

/**
 * 선택된 공지사항 게시글 삭제
 *
 * @param boards
 */
@Transactional
public void deleteBoards(List<BoardNoticeParam> boards) { // 자신이 작성한 글 목록에서 선택해서 삭제 가능
    boards.forEach(board -> {
        BoardNotice b = boardNoticeRepository.findById(board.getId()).orElse(null);

        if (b != null) {
            boardNoticeRepository.delete(b);
        } else {
            throw new IllegalStateException("해당 아이디의 게시글 정보가 존재하지 않습니다.");
        }
    });
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
        notice.updateViewCount(notice.getViews() + 1);
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
public NoticePagenationParam getBoardNoticePagenationByTitleOrContent(int page, Integer size, String keyword) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
    Page<BoardNotice> pages = boardNoticeRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    List<BoardNotice> list = pages.getContent();

    List<BoardNoticeParam> vos = list.stream().map(BoardNoticeParam::create).toList();

    return NoticePagenationParam.builder()
            .totalPageCnt(pages.getTotalPages())
            .boardNoticeList(vos)
            .build();
}
}
