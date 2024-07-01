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

    public CommentImpParam findById(Long id) {
        CommentImp com = commentImpRepository.findOneById(id);

        CommentImpParam vo = null;

        if (com != null) {
            vo = CommentImpParam.create(com);

        } else {
            log.error("해당 아이디의 댓글 정보가 존재하지 않습니다.");
//            throw new IllegalStateException("해당 아이디의 댓글 정보가 존재하지 않습니다.");
        }

        return vo;
    }

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

    public Long updateComment(CommentImpParam commentParam) {
        CommentImp comment = commentImpRepository.findById(commentParam.getId()).orElse(null);

        if (comment != null) {
            comment.updateContent(commentParam.getContent());
        } else {
            throw new IllegalStateException("해당 아이디의 댓글이 존재하지 않습니다.");
        }

        return comment.getId();
    }

    public void deleteCommentById(Long commentId) {
        Optional<CommentImp> comm = commentImpRepository.findById(commentId);
        comm.ifPresent(commentImpRepository::delete);
    }

    public CommentImpPagenationParam getComments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<CommentImp> result = commentImpRepository.findAll(pageable);

        return new CommentImpPagenationParam(result);
    }

    public CommentImpPagenationParam getCommentsByBoard(Long boardId, int page, int size) {
        BoardImp b = boardImpRepository.findOneById(boardId);

        if (b != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
            Page<CommentImp> result = commentImpRepository.findAllByBoard(b, pageable);

            return new CommentImpPagenationParam(result);
        } else {
            log.error("해당 아이디의 댓글 정보가 존재하지 않습니다.");
//            throw new IllegalStateException("해당 아이디의 댓글 정보가 존재하지 않습니다.");

            return CommentImpPagenationParam.builder()
                    .commentImpsList(new ArrayList<>())
                    .totalPageCnt(0)
                    .currentPage(page)
                    .size(size)
                    .totalElementCnt(0)
                    .build();
        }
    }

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
}
