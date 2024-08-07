package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Repository
public interface CommentMovRepository extends JpaRepository<CommentMov, Long> {

    Page<CommentMov> findAllByMovieNo(Long movieNo, Pageable pageable);

    Page<CommentMov> findByWriter(Member member, Pageable pageable);

    @Transactional
    @Modifying
    @Query("delete from CommentMov c where c.writer.memNo = :writerNo")
    void deleteAllByWriterNo(Long writerNo);

    @Transactional
    @Modifying
    @Query("delete from CommentMov c where c.writer.memNo in :writerNos")
    void deleteAllByWriterNos(List<Long> writerNos);

//    /**
//     * 추천수로 정렬(20개씩 조회)
//     * @param movieNo
//     * @param pageable
//     */
//    Page<CommentMov> findAllByMovieNoOrderByRecommended(Long movieNo, Pageable pageable);
}
