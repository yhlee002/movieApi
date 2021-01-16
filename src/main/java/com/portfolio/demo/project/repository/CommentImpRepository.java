package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.comment.CommentImp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentImpRepository extends JpaRepository<CommentImp, Long> {

    @Query("select c from CommentImp c where c.board.id=?1")
    List<CommentImp> findByBoardId(Long boardId);

    List<CommentImp> findByWriter_MemNo(Long memNo);
}
