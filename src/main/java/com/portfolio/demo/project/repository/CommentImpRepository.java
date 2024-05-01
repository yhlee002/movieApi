package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentImpRepository extends JpaRepository<CommentImp, Long> {

    Page<CommentImp> findByBoard(BoardImp board, Pageable pageable);

    Page<CommentImp> findAllByWriter(Member member, Pageable pageable);

    Integer countCommentImpsByWriter(Member member);
}
