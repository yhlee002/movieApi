package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.repository.CommentImpRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.dto.comment.CommentImpPagenationParam;
import com.portfolio.demo.project.dto.comment.CommentImpParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Transactional
@Service
public class CommentImpService {

    private final CommentImpRepository commentImpRepository;

    private final BoardImpRepository boardImpRepository;

    private final MemberRepository memberRepository;


    /**
     * 후기 게시글 댓글 단건 조회
     *
     * @param id 조회하고자하는 댓글 식별번호
     */
    public CommentImpParam findById(Long id) {
        CommentImp com = commentImpRepository.findOneById(id);

        CommentImpParam vo = null;

        if (com != null) {
            vo = CommentImpParam.create(com);

        } else {
            throw new IllegalStateException("해당 아이디의 댓글 정보가 존재하지 않습니다.");
        }

        return vo;
    }

    /**
     * 후기 게시글 댓글 작성
     *
     * @param comment 작성하고자하는 댓글 정보
     */
    public Long saveComment(CommentImpParam comment) {
        Member writer = memberRepository.findById(comment.getWriterId()).orElse(null);
        BoardImp board = boardImpRepository.findById(comment.getBoardId()).orElse(null);

        CommentImp result = CommentImp.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .board(board)
                .writer(writer)
                .build();

        commentImpRepository.save(result);

        return result.getId();
    }

    /**
     * 모든 댓글 조회
     *
     * @param page 페이지 번호
     * @param size 조회할 게시글 수
     */
    public CommentImpPagenationParam getComments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<CommentImp> result = commentImpRepository.findAll(pageable);

        return new CommentImpPagenationParam(result);
    }

    /**
     * 특정 후기 게시글의 댓글 조회
     *
     * @param boardId 후기 게시글 식별번호
     * @param page 페이지 번호
     * @param size 조회할 게시글 수
     */
    public CommentImpPagenationParam getCommentsByBoard(Long boardId, int page, int size) {
        BoardImp b = boardImpRepository.findOneById(boardId);

        if (b != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
            Page<CommentImp> result = commentImpRepository.findAllByBoard(b, pageable);

            return new CommentImpPagenationParam(result);
        } else {
            throw new IllegalStateException("해당 아이디의 게시글 정보가 존재하지 않습니다.");
        }
    }

    /**
     * 특정 회원의 댓글 조회
     *
     * @param memNo 회원 식별번호
     * @param page 페이지 번호
     * @param size 조회할 게시글 수
     */
    public CommentImpPagenationParam getCommentsByMember(Long memNo, int page, int size) {
        Member mem = memberRepository.findById(memNo).orElse(null);

        if (mem != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
            Page<CommentImp> result = commentImpRepository.findAllByWriter(mem, pageable);

            return new CommentImpPagenationParam(result);
        } else {
            log.error("해당 아이디의 회원 정보가 존재하지 않습니다.");
            throw new IllegalStateException("해당 아이디의 회원 정보가 존재하지 않습니다.");
        }
    }

    /**
     * 후기 게시글 댓글 수정
     *
     * @param comment 수정하고자하는 댓글 정보
     */
    public Long updateComment(CommentImpParam comment) {
        CommentImp foundComment = commentImpRepository.findById(comment.getId()).orElse(null);

        if (foundComment != null) {
            foundComment.updateContent(comment.getContent());
        } else {
            throw new IllegalStateException("해당 아이디의 댓글이 존재하지 않습니다.");
        }

        return foundComment.getId();
    }

    /**
     * 후기 게시글 댓글 삭제
     *
     * @param commentId 삭제하고자하는 댓글 식별번호
     */
    public void deleteCommentById(Long commentId) {
        Optional<CommentImp> comm = commentImpRepository.findById(commentId);
        comm.ifPresent(commentImpRepository::delete);
    }

    /**
     * 댓글 단건 삭제(삭제 flag만 변경. 영구 삭제 X)
     *
     * @param id 삭제하고자 하는 댓글 식별번호
     */
    public int updateDelYnById(Long id) {
        return commentImpRepository.updateDelYnById(id);
    }

    /**
     * 복수의 댓글 삭제(삭제 flag만 변경. 영구 삭제 X)
     *
     * @param ids 삭제하고자 하는 댓글 식별번호 목록
     */
    public int updateDelYnByIds(List<Long> ids) {
        return commentImpRepository.updateDelYnByIds(ids);
    }

    /**
     * 복수의 공지사항 게시글 영구 삭제(아이디 사용)
     *
     * @param ids 삭제하고자 하는 게시글의 식별번호 목록
     */
    public void deleteByIds(List<Long> ids) {
        commentImpRepository.deleteByIds(ids);
    }
}
