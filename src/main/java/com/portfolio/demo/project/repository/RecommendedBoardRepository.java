package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.recommended.RecommendedBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RecommendedBoardRepository extends JpaRepository<RecommendedBoard, Long> {

    Boolean existsByBoardIdAndMemNo(Long boardId, Long memNo);

    void deleteByMemNo(Long memNo);

    @Transactional
    @Modifying
    @Query("delete from RecommendedBoard r where r.memNo in :memNos")
    void deleteAllByMemNos(List<Long> memNos);

    void deleteAllByBoardId(Long boardId);

    @Transactional
    @Modifying
    @Query("delete from RecommendedBoard r where r.boardId in :boardIds")
    void deleteAllByBoardIds(List<Long> boardIds);
}
