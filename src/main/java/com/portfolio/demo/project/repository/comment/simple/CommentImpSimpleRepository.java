package com.portfolio.demo.project.repository.comment.simple;

import com.portfolio.demo.project.dto.comment.simple.CommentImpSimpleParam;
import com.portfolio.demo.project.entity.comment.CommentImp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentImpSimpleRepository extends JpaRepository<CommentImp, Long> {

    @Query(value = "select new com.portfolio.demo.project.dto.comment.simple.CommentImpSimpleParam(c.id, b.id, m.memNo, m.name, m.profileImage, c.content, c.regDate)" +
            " from CommentImp c" +
            " join c.writer m" +
            " join c.board b" +
            " where c.board.id in :ids")
    Page<CommentImpSimpleParam> findAllParamsByBoardIds(@Param("ids") List<Long> ids, Pageable pageable);

    @Query(value = "select new com.portfolio.demo.project.dto.comment.simple.CommentImpSimpleParam(c.id, b.id, m.memNo, m.name, m.profileImage, c.content, c.regDate)" +
            " from CommentImp c" +
            " join c.writer m" +
            " join c.board b" +
            " where c.board.id = :id")
    Page<CommentImpSimpleParam> findAllParamsByBoardId(@Param("id") Long id, Pageable pageable);
}
