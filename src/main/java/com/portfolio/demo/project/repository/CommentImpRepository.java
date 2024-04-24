package com.portfolio.demo.project.repository;

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

    List<CommentImp> findByBoard(BoardImp board);

    Page<CommentImp> findAllByWriter(Member member, Pageable pageable);

    @Query("select c from CommentImp c join Member m on c.writer = m where m = :member order by c.regDate desc limit :size")
    List<CommentImp> findRecentCommentImpsByWriter(@Param("member") Member member, @Param("size") int size);

    Long countCommentImpsByWriter(Member member);

    List<CommentImp> findByWriterOrderByRegDateDesc(Member member, Pageable pageable);
}
