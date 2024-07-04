package com.portfolio.demo.project.service;

import com.portfolio.demo.project.dto.board.*;
import com.portfolio.demo.project.dto.comment.CommentImpParam;
import com.portfolio.demo.project.dto.comment.count.CommentCount;
import com.portfolio.demo.project.dto.member.MemberPagenationParam;
import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.repository.CommentImpRepository;
import com.portfolio.demo.project.dto.comment.simple.CommentImpSimpleParam;
import com.portfolio.demo.project.repository.comment.count.CommentImpCountRepository;
import com.portfolio.demo.project.repository.comment.simple.CommentImpSimpleRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class BoardImpService {

    private final BoardImpRepository boardImpRepository;

    private final MemberRepository memberRepository;

    private final CommentImpRepository commentImpRepository;

    private final CommentImpSimpleRepository commentImpSimpleRepository;

    private final CommentImpCountRepository commentImpCountRepository;

    /**
     * 전체 감상평 게시글 조회
     */
    public ImpressionPagenationParam getAllBoards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<BoardImp> pages = boardImpRepository.findAll(pageable);

        List<BoardImp> list = pages.getContent();
        List<BoardImpParam> vos = list.stream().map(BoardImpParam::create).collect(Collectors.toList());
        vos.forEach(vo -> {
            int commentSize = commentImpRepository.findCountByBoardId(vo.getId());
            vo.setCommentSize(commentSize);
        });

        return ImpressionPagenationParam.builder()
                .totalPageCnt(pages.getTotalPages())
                .currentPage(page)
                .size(size)
                .totalElementCnt(pages.getTotalElements())
                .boardImpList(vos)
                .build();
    }

    /**
     * 특정 회원의 게시글 조회
     */
    public ImpressionPagenationParam getByMemNo(Long memNo, int page, int size) {
        Member member = memberRepository.findById(memNo).orElse(null);

        ImpressionPagenationParam param = new ImpressionPagenationParam();
        if (member != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
            Page<BoardImp> boardImpPage = boardImpRepository.findAllByWriter(member, pageable);
            param = new ImpressionPagenationParam(boardImpPage);
        } else {
            throw new IllegalStateException("해당 아이디의 회원 정보가 존재하지 않습니다.");
        }

        return param;
    }

    /**
     * 감상평 게시글 식별번호로 단건 조회
     *
     * @param id
     * @return
     */
    public BoardImpParam findById(Long id) {
        BoardImp boardImp = boardImpRepository.findOneById(id);

        BoardImpParam vo = null;

        if (boardImp != null) {
            vo = BoardImpParam.create(boardImp);

            Pageable pageable = PageRequest.of(0, 15, Sort.by("regDate").descending());
            Page<CommentImpSimpleParam> commentPage = commentImpSimpleRepository.findAllParamsByBoardId(id, pageable);
            List<CommentImpSimpleParam> result = commentPage.getContent();

            List<CommentImpParam> comments = result.stream().map(simple -> CommentImpParam.builder()
                    .id(simple.getId())
                    .boardId(simple.getBoardId())
                    .writerId(simple.getWriterId())
                    .writerName(simple.getWriterName())
                    .writerProfileImage(simple.getWriterProfileImage())
                    .content(simple.getContent())
                    .regDate(simple.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build()).collect(Collectors.toList());

            vo.setComments(comments);
        }

        return vo;
    }

    /**
     * 감상평 게시글 식별번호로 이전글 조회
     *
     * @param id
     * @return
     */
    public BoardImpParam findPrevById(Long id) {
        BoardImp boardImp = boardImpRepository.findPrevBoardImpById(id);

        BoardImpParam vo = null;

        if (boardImp != null) {
            vo = BoardImpParam.create(boardImp);

            Pageable pageable = PageRequest.of(0, 15, Sort.by("regDate").descending());
            Page<CommentImpSimpleParam> commentPage = commentImpSimpleRepository.findAllParamsByBoardId(id, pageable);
            List<CommentImpSimpleParam> result = commentPage.getContent();

            List<CommentImpParam> comments = result.stream().map(simple -> CommentImpParam.builder()
                    .id(simple.getId())
                    .boardId(simple.getBoardId())
                    .writerId(simple.getWriterId())
                    .writerName(simple.getWriterName())
                    .writerProfileImage(simple.getWriterProfileImage())
                    .content(simple.getContent())
                    .regDate(simple.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build()).collect(Collectors.toList());

            vo.setComments(comments);
        }
        return vo;
    }

    /**
     * 감상평 게시글 식별번호로 다음글 조회
     *
     * @param id
     * @return
     */
    public BoardImpParam findNextById(Long id) {
        BoardImp boardImp = boardImpRepository.findNextBoardImpById(id);

        BoardImpParam vo = null;

        if (boardImp != null) {
            vo = BoardImpParam.create(boardImp);

            Pageable pageable = PageRequest.of(0, 15, Sort.by("regDate").descending());
            Page<CommentImpSimpleParam> commentPage = commentImpSimpleRepository.findAllParamsByBoardId(id, pageable);
            List<CommentImpSimpleParam> result = commentPage.getContent();

            List<CommentImpParam> comments = result.stream().map(simple -> CommentImpParam.builder()
                    .id(simple.getId())
                    .boardId(simple.getBoardId())
                    .writerId(simple.getWriterId())
                    .writerName(simple.getWriterName())
                    .writerProfileImage(simple.getWriterProfileImage())
                    .content(simple.getContent())
                    .regDate(simple.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build()).collect(Collectors.toList());

            vo.setComments(comments);
        }

        return vo;
    }

    /**
     * 인기 감상평 게시글 top {size} 조회
     */
    public List<BoardImpParam> getMostFavImpBoard(int size) {
        List<BoardImp> mostFavImpBoards = boardImpRepository.findMostFavImpBoards(size);
        List<BoardImpParam> boardParams = mostFavImpBoards.stream().map(BoardImpParam::create).collect(Collectors.toList());

        for (BoardImpParam param : boardParams) {
            int commentSize = commentImpRepository.findCountByBoardId(param.getId());
            param.setCommentSize(commentSize);
        }

        return boardParams;
    }

    /**
     *
     * @param page 페이지 번호
     * @param size 조회할 게시글 수
     * @param condition 정렬 기준(2024.06 기준 views)
     * @return
     */
    public ImpressionPagenationParam getAllBoardsOrderByCondition(int page, Integer size, String condition) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(condition).descending());
        Page<BoardImp> result = boardImpRepository.findAll(pageable);

        return new ImpressionPagenationParam(result);
    }

    public ImpressionPagenationParam getAllBoardsOrderByCommentSizeDesc(int page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BoardImp> pages = boardImpRepository.findAllOrderByCommentsCountDesc(pageable);
        List<BoardImp> list = pages.getContent();

        List<Long> ids = list.stream().map(imp -> imp.getId()).collect(Collectors.toList());
        List<CommentCount> commentCounts = commentImpCountRepository.findCommentCountsByBoardIds(ids);
        Map<Long, Long> commentCountMap = commentCounts.stream().collect(Collectors.toMap(CommentCount::getBoardId, CommentCount::getCount));

        List<BoardImpParam> vos = list.stream().map(BoardImpParam::create).toList();
        for (BoardImpParam board : vos) {
            Long count = commentCountMap.get(board.getId());
            if (count != null) {
                board.setCommentSize(count.intValue());
            } else {
                board.setCommentSize(0);
            }
        }

        return ImpressionPagenationParam.builder()
                .totalPageCnt(pages.getTotalPages())
                .currentPage(page)
                .size(size)
                .totalElementCnt(pages.getTotalElements())
                .boardImpList(vos)
                .build();
    }

    /**
     * 감상평 게시글 작성
     *
     * @param boardParam
     */
    public Long saveBoard(BoardImpParam boardParam) {

        Member user = memberRepository.findById(boardParam.getWriterId()).orElse(null);

        BoardImp board = BoardImp.builder()
                .title(boardParam.getTitle())
                .content(boardParam.getContent())
                .writer(user)
                .views(0)
                .recommended(0)
                .build();
        boardImpRepository.save(board);

        return board.getId();
    }

    /**
     * 감상평 게시글 수정
     *
     * @param boardParam
     */
    public Long updateBoard(BoardImpParam boardParam) {
        // 작성자 정보 검증
        BoardImp board = boardImpRepository.findById(boardParam.getId()).orElse(null);

        if (board != null) {
            board.updateTitle(boardParam.getTitle());
            board.updateContent(boardParam.getContent());
        } else {
            throw new IllegalStateException("해당 아이디의 회원 정보가 존재하지 않습니다.");
//            log.error("해당 아이디의 회원 정보가 존재하지 않습니다. (memNo: {})", imp.getWriterId());
        }

        return board.getId();
    }

    /**
     * 감상평 게시글 삭제
     *
     * @param id
     */
    public void deleteById(Long id) {
        BoardImp board = boardImpRepository.findById(id).orElse(null);
        if (board != null) {
            boardImpRepository.delete(board);
        } else {
            throw new IllegalStateException("해당 아이디의 게시글 정보가 존재하지 않습니다.");
        }
    }

    /**
     * 감상평 게시글 추천수 업데이트
     *
     * @param id
     */
    public void upViewCntById(Long id) {
        BoardImp imp = boardImpRepository.findById(id).orElse(null);
        if (imp != null) {
            imp.updateViewCount(imp.getViews() + 1);
        } else {
            throw new IllegalStateException("해당 아이디의 게시글 정보가 존재하지 않습니다.");
        }
    }

    /**
     * 복수의 감상평 게시글 삭제
     *
     * @param boards
     */
    public void deleteBoards(List<BoardImpParam> boards) { // 자신이 작성한 글 목록에서 선택해서 삭제 가능
        List<BoardImp> list = boards.stream().map(b -> {
            Member member = memberRepository.findById(b.getWriterId()).orElse(null);

            return BoardImp.builder()
                    .id(b.getId())
                    .title(b.getTitle())
                    .content(b.getContent())
                    .writer(member)
                    .views(b.getViews())
                    .recommended(b.getRecommended())
                    .build();
        }).toList();

        boardImpRepository.deleteAll(list);
    }

    /**
     * 감상평 게시글 조회
     *
     * @param page
     * @return
     */
    public ImpressionPagenationParam getImpPagenation(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<BoardImp> pages = boardImpRepository.findAll(pageable);

        List<BoardImpParam> list = pages.getContent().stream().map(BoardImpParam::create).toList();
        list.forEach(board -> {
            int countByBoardId = commentImpRepository.findCountByBoardId(board.getId());
            board.setCommentSize(countByBoardId);
        });

        return ImpressionPagenationParam.builder()
                .totalPageCnt(pages.getTotalPages())
                .currentPage(page)
                .size(size)
                .totalElementCnt(pages.getTotalElements())
                .boardImpList(list)
                .build();
    }

    /**
     * 검색 기능 (작성자명)
     *
     * @param page
     * @param keyword
     */
    public ImpressionPagenationParam getImpPagenationByWriterName(int page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<BoardImp> pages = boardImpRepository.findByWriterNameContainingIgnoreCaseOrderByRegDateDesc(keyword, pageable);

        List<BoardImpParam> list = pages.getContent().stream().map(BoardImpParam::create).toList();
        list.forEach(board -> {
            int countByBoardId = commentImpRepository.findCountByBoardId(board.getId());
            board.setCommentSize(countByBoardId);
        });

        return ImpressionPagenationParam.builder()
                .totalPageCnt(pages.getTotalPages())
                .currentPage(page)
                .size(size)
                .totalElementCnt(pages.getTotalElements())
                .boardImpList(list)
                .build();
    }

    /**
     * 검색 기능 (제목 또는 내용)
     *
     * @param page
     * @param keyword
     */
    public ImpressionPagenationParam getImpPagenationByTitleOrContent(int page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<BoardImp> pages = boardImpRepository.findAllByTitleContainingOrContentContaining(keyword, keyword, pageable);

        List<BoardImpParam> list = pages.getContent().stream().map(BoardImpParam::create).toList();
        list.forEach(board -> {
            int countByBoardId = commentImpRepository.findCountByBoardId(board.getId());
            board.setCommentSize(countByBoardId);
        });

        return ImpressionPagenationParam.builder()
                .totalPageCnt(pages.getTotalPages())
                .currentPage(page)
                .size(size)
                .totalElementCnt(pages.getTotalElements())
                .boardImpList(list)
                .build();

    }

    /**
     * 특정 작성자가 작성한 글(마이페이지에서 조회 가능)
     *
     * @param memNo
     * @param page
     * @param size
     */
    public ImpressionPagenationParam getImpsByMember(Long memNo, int page, int size) {
        Member member = memberRepository.findById(memNo).orElse(null);

        ImpressionPagenationParam param = new ImpressionPagenationParam();

        if (member != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
            Page<BoardImp> result = boardImpRepository.findAllByWriter(member, pageable);
            List<BoardImp> list = result.getContent();

            List<BoardImpParam> params = list.stream().map(BoardImpParam::create).toList();

            params.forEach(board -> {
                int countByBoardId = commentImpRepository.findCountByBoardId(board.getId());
                board.setCommentSize(countByBoardId);
            });

            param = ImpressionPagenationParam.builder()
                    .totalPageCnt(result.getTotalPages())
                    .totalElementCnt(result.getTotalElements())
                    .currentPage(page)
                    .size(size)
                    .boardImpList(params)
                    .build();

        } else {
            throw new IllegalStateException("해당 아이디의 회원 정보가 존재하지 않습니다.");
        }

        return param;
    }

}
