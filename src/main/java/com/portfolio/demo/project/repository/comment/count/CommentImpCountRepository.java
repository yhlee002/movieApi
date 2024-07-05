package com.portfolio.demo.project.repository.comment.count;

import com.portfolio.demo.project.dto.comment.count.CommentCount;
import com.portfolio.demo.project.entity.comment.CommentImp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentImpCountRepository extends JpaRepository<CommentImp, Long> {

    @Query("select new com.portfolio.demo.project.dto.comment.count.CommentCount(c.board.id, count(c.id))" +
            " from CommentImp c" +
            " join c.board" +
            " where c.delYn is null or c.delYn <> 'Y'" +
            " group by c.board.id")
    List<CommentCount> findCommentCountsByBoardIds(List<Long> ids);
}

