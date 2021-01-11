package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.comment.CommentMov;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMovRepository extends JpaRepository<CommentMov, Long> {

    @Query(value = "select c, m.name from CommentMov c join Member m on c.writerNo=m.memNo")
    List<CommentMov> findAll();

    @Query(value = "select count(c) from CommentMov c")
    int findAllCommentMovsCnt();
    // 추천수로 정렬(20개씩 조회)
    @Query(value = "select c, m.name from movie_comment c join member m order by c.recommended desc limit ?1, ?2", nativeQuery = true)
    List<CommentMov> findCommentMovsByOrderByRecommended(int startRow, int CommentCntPerPage);

    // 최신순으로 정렬(20개씩 조회)
    @Query(value = "select c, m.name from movie_comment c join member m order by c.regDate desc limit ?1, ?2", nativeQuery = true)
    List<CommentMov> findCommentMovsByOrderByRegDate(int startRow, int CommentCntPerPage);
}
