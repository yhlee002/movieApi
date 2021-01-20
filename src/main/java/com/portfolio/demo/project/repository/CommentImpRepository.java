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

    List<CommentImp> findTop5ByWriter_MemNoOrderByRegDateDesc(Long memNo);

    @Query("select count(c) from CommentImp c where c.writer.memNo=?1")
    Long findCountByWriter_MemNo(Long memNo);

    @Query(value = "select c.* from comment_imp c where c.writer_no = ?1 order by c.id desc limit ?2, ?3"
            , nativeQuery = true)
    List<CommentImp> findCommImpListViewByWriterNo(Long memNo, int startRow, int COMMENT_COUNT_PER_PAGE);



}
