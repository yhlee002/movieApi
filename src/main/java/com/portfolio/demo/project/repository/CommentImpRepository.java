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
            " where c.board = :board and c.delYn != 'Y'")
    Page<CommentImp> findAllByBoard(@Param("board") BoardImp board, Pageable pageable);

    @Query("select c from CommentImp c" +
            " join fetch c.writer m" +
            " where c.board.id = :boardId and c.delYn != 'Y'")
    Page<CommentImp> findAllByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    @Query("select c from CommentImp c" +
            " join fetch c.writer m" +
            " where c.id = :id and c.delYn != 'Y'")
    CommentImp findOneById(@Param("id") Long id);

    @Query("select count(c) from CommentImp c" +
            " where c.board.id = :id and c.delYn != 'Y'")
    int findCountByBoardId(@Param("id") Long boardId);

    @Query("select c from CommentImp c" +
            " join fetch c.writer m" +
            " join fetch c.board b" +
            " where c.writer = :member and c.delYn != 'Y'")
    Page<CommentImp> findAllByWriter(Member member, Pageable pageable);

    @Query("select count(c) from CommentImp c" +
            " join c.writer m" +
            " join c.board b" +
            " where c.writer = :member and c.delYn != 'Y'")
    Integer countCommentImpsByWriter(Member member);

    @Transactional
    @Modifying
    @Query("update CommentImp c set c.delYn = 'Y' where c.id = :id")
    int updateDelYnById(Long id);

    @Transactional
    @Modifying
    @Query("update CommentImp c set c.delYn = 'Y' where c.id in :ids")
    int updateDelYnByIds(List<Long> ids);

    @Transactional
    @Modifying
    @Query("delete from CommentImp b where b.id in :ids")
    void deleteByIds(List<Long> ids);
}
