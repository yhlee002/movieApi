package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentImpRepository extends JpaRepository<CommentImp, Long> {

    List<CommentImp> findByBoard(BoardImp board);

    Page<CommentImp> findAllByWriter(Member member, Pageable pageable);

    List<CommentImp> findTop5ByWriter_MemNoOrderByRegDateDesc(Long memNo);

    Long countCommentImpsByWriter(Member member);

    @Query(value = "select c.* from comment_imp c where c.writer_no = ?1 order by c.id desc limit ?2, ?3"
            , nativeQuery = true)
    List<CommentImp> findCommentImpsByWriterNo(Long memNo, int startRow, int COMMENT_COUNT_PER_PAGE);



}
