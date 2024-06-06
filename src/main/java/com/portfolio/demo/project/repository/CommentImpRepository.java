package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.dto.CommentImpParam;
import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentImpRepository extends JpaRepository<CommentImp, Long> {



    Page<CommentImp> findAllByBoard(BoardImp board, Pageable pageable);

    Page<CommentImp> findAllByBoardId(@Param("board_id") Long boardId, Pageable pageable);

    @Query(value = "select count(c) from CommentImp c" +
            " where c.board.id = :id")
    int findCountByBoardId(@Param("id") Long boardId);

    Page<CommentImp> findAllByWriter(Member member, Pageable pageable);

    Integer countCommentImpsByWriter(Member member);
}
