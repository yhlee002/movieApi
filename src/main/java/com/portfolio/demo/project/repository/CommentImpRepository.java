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

    @Query(value = "select new com.portfolio.demo.project.dto.CommentImpParam(c.id, b.id, m.memNo, m.name, c.content, c.regDate)" +
            " from CommentImp c" +
            " join c.writer m" +
            " join c.board b" +
            " where c.board.id in :ids")
    Page<CommentImpParam> findAllParamsByBoardIds(@Param("ids") List<Long> ids, Pageable pageable);

    @Query(value = "select new com.portfolio.demo.project.dto.CommentImpParam(c.id, b.id, m.memNo, m.name, c.content, c.regDate)" +
            " from CommentImp c" +
            " join c.writer m" +
            " join c.board b" +
            " where c.board.id = :id")
    Page<CommentImpParam> findAllParamsByBoardId(@Param("id") Long id, Pageable pageable);

    Page<CommentImp> findAllByBoard(BoardImp board, Pageable pageable);

    Page<CommentImp> findAllByBoardId(@Param("board_id") Long boardId, Pageable pageable);

    Page<CommentImp> findAllByWriter(Member member, Pageable pageable);

    Integer countCommentImpsByWriter(Member member);
}
