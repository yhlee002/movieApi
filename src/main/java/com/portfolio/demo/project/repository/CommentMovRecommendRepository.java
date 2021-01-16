package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.comment.CommentMovRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMovRecommendRepository extends JpaRepository<CommentMovRecommend, Long> {

    List<CommentMovRecommend> findByCommentId(Long commentId);

    List<CommentMovRecommend> findByWriter_MemNo(Long memNo);

    @Query(value = "select cmr.comment.id from CommentMovRecommend cmr where cmr.writer.memNo=?1")
    List<Long> findCommentIdByMemNo(Long memNo);

    @Query(value = "select cmr from CommentMovRecommend cmr where cmr.comment.id=?1 and cmr.writer.memNo=?2")
    CommentMovRecommend findByCommentIdAndMemNo(Long commentId, Long memNo);


}
