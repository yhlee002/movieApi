package com.portfolio.demo.project.service;

import com.portfolio.demo.project.dto.board.ImpressionPagenationParam;
import com.portfolio.demo.project.entity.board.BoardImp;
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
@Transactional
@Service
public class BoardNoticeService {

    private final BoardNoticeRepository boardNoticeRepository;

    private final MemberRepository memberRepository;

    /**
     * 전체 공지사항 게시글 조회
     *
     * @param page 페이지 번호
     * @param size 페이지당 보여줄 데이터 수
     * @return 게시글 전체 조회(조건X)
     */
    public NoticePagenationParam getAllBoards(int page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regDate"));
        Page<BoardNotice> result = boardNoticeRepository.findAll(pageable);

        return new NoticePagenationParam(result);
    }

    /**
     * 특정 회원의 게시글 조회
     */
    public NoticePagenationParam getByMemNo(Long memNo, int page, int size) {
        Member member = memberRepository.findById(memNo).orElse(null);

        NoticePagenationParam param = new NoticePagenationParam();
        if (member != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
            Page<BoardNotice> boardNoticePage = boardNoticeRepository.findAllByWriter(member, pageable);
            param = new NoticePagenationParam(boardNoticePage);
        } else {
            throw new IllegalStateException("해당 아이디의 회원 정보가 존재하지 않습니다.");
        }

        return param;
    }

    /**
     * 공지사항 게시글 식별번호로 단건 조회
     *
     * @param boardId 게시글 식별번호
     * @return 단건 공지 게시글
     */
    public BoardNoticeParam findById(Long boardId) {
        BoardNotice board = boardNoticeRepository.findOneById(boardId);

        if (board != null) {
            return BoardNoticeParam.create(board);
        } else {
            log.error("해당 식별번호의 게시글 정보가 존재하지 않습니다.");
        }

        return null;
    }

    /**
     * 공지사항 게시글 식별번호로 이전글 조회
     *
     * @param id 게시글 식별번호
     * @return 단건 공지 게시글
     */
    public BoardNoticeParam findPrevById(Long id) {
        BoardNotice board = boardNoticeRepository.findPrevBoardNoticeById(id);

        if (board != null) {
            return BoardNoticeParam.create(board);
        }
        return null;
    }

    /**
     * 공지사항 게시글 식별번호로 다음글 조회
     *
     * @param id 게시글 식별번호
     * @return 단건 공지 게시글
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
     *
     * @param size 조회할 게시글 수
     * @return 최근 공지사항 게시글 {size}개
     */
    public List<BoardNoticeParam> getRecentNoticeBoards(int size) {
        List<BoardNotice> result = boardNoticeRepository.findRecentBoardNoticesOrderByRegDate(size);
        return result.stream().map(BoardNoticeParam::create).toList();
    }

    /**
     * 공지사항 게시글 조회(검색어가 존재)
     *
     * @param page 페이지 번호
     * @param keyword 조회할 게시글 수
     */
    public NoticePagenationParam getBoardNoticePagenationByTitleOrContent(int page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<BoardNotice> result = boardNoticeRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);

        return new NoticePagenationParam(result);
    }

    /**
     * 정렬 조건에 따른 게시글 조회
     *
     * @param page 페이지 번호
     * @param size 조회할 게시글 수
     * @param orderby 정렬 기준(2024.06 기준 views)
     * @return
     */
    public NoticePagenationParam getAllBoardsOrderByCondition(int page, Integer size, String orderby) {
        Sort sort = null;
        if ("regdate".equals(orderby.toLowerCase())) {
            sort = Sort.by("regDate").descending();
        } else if ("views".equals(orderby.toLowerCase())){
            sort = Sort.by("views").descending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BoardNotice> pages = boardNoticeRepository.findAll(pageable);
        List<BoardNotice> list = pages.getContent();

        List<BoardNoticeParam> vos = list.stream().map(BoardNoticeParam::create).toList();

        return NoticePagenationParam.builder()
                .totalPageCnt(pages.getTotalPages())
                .currentPage(page)
                .size(size)
                .totalElementCnt(pages.getTotalElements())
                .boardNoticeList(vos)
                .build();
    }

    /**
     * 공지사항 게시글 작성
     *
     * @param param 게시글 정보
     * @return 생성된 게시글 식별번호
     */
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
     * @param param 게시글 정보
     */
    public Long updateBoard(BoardNoticeParam param) {
        BoardNotice board = boardNoticeRepository.findById(param.getId()).orElse(null);

        if (board != null) {
            board.updateTitle(param.getTitle());
            board.updateContent(param.getContent());
        } else {
            throw new IllegalStateException("해당 식별번호의 게시글 정보가 존재하지 않습니다.");
        }

        return board.getId();
    }

    /**
     * 공지사항 게시글 삭제
     *
     * @param boardId 게시글 식별번호
     */
    public void deleteBoardByBoardId(Long boardId) {
        BoardNotice board = boardNoticeRepository.findById(boardId).orElse(null);

        if (board != null) {
            boardNoticeRepository.delete(board);
        } else {
            throw new IllegalStateException("해당 식별번호의 게시글 정보가 존재하지 않습니다.");
        }
    }

    /**
     * 단건의 공지사항 게시글 삭제(삭제 flag만 변경. 영구 삭제 X)
     *
     * @param id
     */
    public int updateDelYnById(Long id) {
        return boardNoticeRepository.updateDelYnById(id);
    }

    /**
     * 복수의 공지사항 게시글 삭제(삭제 flag만 변경. 영구 삭제 X)
     *
     * @param ids
     */
    public int updateDelYnByIds(List<Long> ids) {
        return boardNoticeRepository.updateDelYnByIds(ids);
    }

    /**
     * 복수의 공지사항 게시글 삭제(아이디 사용)
     * @param ids 삭제하고자 하는 게시글의 식별번호 목록
     */
    public void deleteByIds(List<Long> ids) {
        boardNoticeRepository.deleteByIds(ids);
    }

    /**
     * 선택된 공지사항 게시글 삭제
     *
     * @param boards 삭제할 게시글 목록
     */
    public void deleteBoards(List<BoardNoticeParam> boards) { // 자신이 작성한 글 목록에서 선택해서 삭제 가능
        boards.forEach(board -> {
            BoardNotice b = boardNoticeRepository.findById(board.getId()).orElse(null);

            if (b != null) {
                boardNoticeRepository.delete(b);
            } else {
                throw new IllegalStateException("해당 식별번호의 게시글 정보가 존재하지 않습니다.");
            }
        });
    }

    /**
     * 공지사항 게시글 조회수 증가
     *
     * @param boardId 게시글 식별번호
     */

    public void upViewCntById(Long boardId) {
        BoardNotice notice = boardNoticeRepository.findOneById(boardId);

        if (notice != null) {
            notice.updateViewCount(notice.getViews() + 1);
        } else {
            throw new IllegalStateException("해당 식별번호의 게시글 정보가 존재하지 않습니다.");
        }
    }
}
