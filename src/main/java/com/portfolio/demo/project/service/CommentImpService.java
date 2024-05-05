package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.repository.CommentImpRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.vo.CommentImpVO;
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
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentImpService {

    private final int COMMENT_COUNT_PER_PAGE = 20;

    private final CommentImpRepository commentImpRepository;

    public CommentImp getCommentById(Long id) {
        return commentImpRepository.findById(id).orElse(null);
    }

    public CommentImp saveComment(CommentImp comment) {
        return commentImpRepository.save(comment);
    }

    public void updateComment(CommentImp comment) {
        Optional<CommentImp> opt = commentImpRepository.findById(comment.getId());
        opt.ifPresentOrElse(
                comm -> {
                    comm.updateContent(comment.getContent());
                    commentImpRepository.save(comm);
                },
                () -> {
                    throw new IllegalStateException("존재하지 않는 댓글입니다.");
                }
        );
    }

    public void deleteCommentById(Long commentId) {
        Optional<CommentImp> comm = commentImpRepository.findById(commentId);
        comm.ifPresent(commentImpRepository::delete);
    }

    public List<CommentImp> getCommentsByBoard(BoardImp board, int page) {
        Pageable pageable = PageRequest.of(page, COMMENT_COUNT_PER_PAGE, Sort.by("regDate").descending());
        Page<CommentImp> result = commentImpRepository.findByBoard(board, pageable);

        return result.getContent();
    }

    public List<CommentImp> getCommentsByMember(Member member, int page) {
        Pageable pageable = PageRequest.of(page, COMMENT_COUNT_PER_PAGE, Sort.by("regDate").descending());
        Page<CommentImp> result = commentImpRepository.findAllByWriter(member, pageable);

        return result.getContent();
    }
}
