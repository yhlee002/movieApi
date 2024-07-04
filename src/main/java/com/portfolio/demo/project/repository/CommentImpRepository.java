package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Query("select count(c) from CommentImp c" +
            " where c.board.id = :id")
    int findCountByBoardId(@Param("id") Long boardId);

    @Query("select c from CommentImp c" +
            " join fetch c.writer m" +
            " join fetch c.board b" +
            " where c.writer = :member")
    Page<CommentImp> findAllByWriter(Member member, Pageable pageable);

    Integer countCommentImpsByWriter(Member member);

    @Transactional
    @Modifying
    @Query("delete from CommentImp b where b.id in :ids")
    void deleteByIds(List<Long> ids);
}
