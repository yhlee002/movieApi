package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.comment.CommentMov;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMovRepository extends JpaRepository<CommentMov, Long> {

    @Query(value = "select c, m.name from CommentMov c join Member m on c.writer.memNo=m.memNo")
    List<CommentMov> findAll();

    List<CommentMov> findByMovieNo(Long movieNo);

    List<CommentMov> findByWriter_MemNo(Long memNo);

    // 추천수로 정렬(20개씩 조회)
    @Query(value = "select c.* from comment_movie c where c.movie_no=?1 order by c.recommended desc limit ?2, ?3",
            nativeQuery = true)
    List<CommentMov> findCommentMovsByOrderByRecommended(Long movieCd, int startRow, int CommentCntPerPage);

    // 최신순으로 정렬(20개씩 조회)
    @Query(value = "select c.* from comment_movie c where c.movie_no=?1 order by c.reg_dt desc limit ?2, ?3 ",
            nativeQuery = true)
    List<CommentMov> findCommentMovsByOrderByRegDate(Long movieCd, int startRow, int CommentCntPerPage);

    Long countByMovieNo(Long movieNo);

}
