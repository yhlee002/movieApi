package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentImpRepository extends JpaRepository<CommentImp, Long> {

    @Query("select c from CommentImp c" +
            " join fetch c.writer m" +
            " where c.board = :board")
    Page<CommentImp> findAllByBoard(@Param("board") BoardImp board, Pageable pageable);

    @Query("select c from CommentImp c" +
            " join fetch c.writer m" +
            " where c.board.id = :boardId")
    Page<CommentImp> findAllByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    @Query("select c from CommentImp c" +
            " join fetch c.writer m" +
            " where c.id = :id")
    CommentImp findOneById(@Param("id") Long id);

    @Query(value = "select count(c) from CommentImp c" +
            " where c.board.id = :id")
    int findCountByBoardId(@Param("id") Long boardId);

    Page<CommentImp> findAllByWriter(Member member, Pageable pageable);

    Integer countCommentImpsByWriter(Member member);
}
